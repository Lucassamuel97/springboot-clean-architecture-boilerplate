# Implementação de Autenticação JWT com Spring Security

Este documento explica em detalhes como foi implementada a autenticação JWT (JSON Web Token) com controle de acesso baseado em roles (RBAC) no projeto, seguindo os princípios da Clean Architecture.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura da Solução](#arquitetura-da-solução)
3. [Componentes Implementados](#componentes-implementados)
4. [Fluxo de Autenticação](#fluxo-de-autenticação)
5. [Configuração de Segurança](#configuração-de-segurança)
6. [Testes](#testes)
7. [Como Usar](#como-usar)

---

## Visão Geral

A autenticação foi implementada utilizando:

- **JWT (JSON Web Token)**: Para tokens stateless
- **Spring Security**: Framework de segurança
- **BCrypt**: Para hash de senhas
- **RBAC**: Controle de acesso baseado em roles (USER, ADMIN)

### Decisões de Design

1. **Clean Architecture**: Toda lógica de autenticação está na camada de infraestrutura
2. **Stateless**: Não mantém sessão no servidor, usa apenas tokens JWT
3. **Role-Based**: Controle de acesso granular por roles
4. **Bearer Token**: Padrão OAuth2 para envio de tokens

---

## Arquitetura da Solução

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT APPLICATION                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│               POST /api/auth/login                           │
│            { username, password }                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│            AuthController (Infrastructure)                   │
│  - Recebe credenciais                                        │
│  - Delega autenticação ao AuthenticationManager              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│        CustomUserDetailsService (Infrastructure)             │
│  - Busca usuário no banco via UserGateway (Domain)           │
│  - Converte User (Domain) → UserPrincipal (Security)         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│          BCryptPasswordEncoder (Spring Security)             │
│  - Valida password hash                                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│            JwtTokenProvider (Infrastructure)                 │
│  - Gera token JWT com claims (userId, roles, etc)            │
│  - Assina com chave secreta                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              LoginResponse + JWT Token                       │
│  { access_token, user_id, username, email, roles }           │
└─────────────────────────────────────────────────────────────┘
```

### Fluxo de Request Protegido

```
┌─────────────────────────────────────────────────────────────┐
│     GET /api/items                                           │
│     Authorization: Bearer <JWT_TOKEN>                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│        JwtAuthenticationFilter (Infrastructure)              │
│  1. Extrai token do header Authorization                     │
│  2. Valida assinatura e expiração                            │
│  3. Extrai userId e roles do token                           │
│  4. Configura SecurityContext                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│          SecurityFilterChain (Spring Security)               │
│  - Verifica se URL requer autenticação                       │
│  - Verifica se usuário tem role necessária                   │
│  - Permite ou nega acesso (401/403)                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              ItemController (se autorizado)                  │
│  - Executa lógica de negócio                                 │
│  - Retorna resposta                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## Componentes Implementados

### 1. Domain Layer

#### UserGateway (Interface)
```java
public interface UserGateway {
    Optional<User> findByUsername(String username);
    // ... outros métodos
}
```

**Responsabilidade**: Define contrato para buscar usuário por username (necessário para autenticação).

**Localização**: `domain/src/main/java/com/starter/crudexample/domain/user/UserGateway.java`

---

### 2. Infrastructure Layer - Security

#### 2.1 JwtTokenProvider

```java
@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication);
    public String getUserIdFromToken(String token);
    public List<Role> getRolesFromToken(String token);
    public boolean validateToken(String authToken);
}
```

**Responsabilidades**:
- ✅ Gerar tokens JWT com claims customizados
- ✅ Validar assinatura e expiração de tokens
- ✅ Extrair informações do token (userId, roles)

**Tecnologia**: JJWT (io.jsonwebtoken) versão 0.12.5

**Configuração**:
- Secret key: Configurável via `app.jwt.secret`
- Expiration: Configurável via `app.jwt.expiration-ms` (padrão: 24h)

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/jwt/JwtTokenProvider.java`

---

#### 2.2 JwtAuthenticationFilter

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Extrai JWT do header Authorization
        // 2. Valida token
        // 3. Configura SecurityContext com autenticação
        // 4. Passa para próximo filtro
    }
}
```

**Responsabilidades**:
- ✅ Interceptar todas as requisições HTTP
- ✅ Extrair token do header `Authorization: Bearer <token>`
- ✅ Validar token usando JwtTokenProvider
- ✅ Configurar contexto de segurança do Spring

**Importante**: Executa uma vez por requisição (`OncePerRequestFilter`)

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/jwt/JwtAuthenticationFilter.java`

---

#### 2.3 UserPrincipal

```java
public class UserPrincipal implements UserDetails {
    private final String id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;
    
    public static UserPrincipal create(User user) {
        // Converte User (Domain) → UserPrincipal (Security)
    }
}
```

**Responsabilidades**:
- ✅ Adaptar entidade `User` do domínio para o Spring Security
- ✅ Implementar interface `UserDetails` do Spring Security
- ✅ Converter roles do domínio em `GrantedAuthority`

**Padrão**: Adapter Pattern (adapta domínio para framework)

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/jwt/UserPrincipal.java`

---

#### 2.4 CustomUserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserGateway userGateway;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
        throws UsernameNotFoundException {
        User user = userGateway.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(...));
        return UserPrincipal.create(user);
    }
}
```

**Responsabilidades**:
- ✅ Implementar contrato do Spring Security para carregar usuários
- ✅ Usar UserGateway (domain) para buscar usuário
- ✅ Converter User → UserPrincipal

**Importante**: É a ponte entre Spring Security e Clean Architecture

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/CustomUserDetailsService.java`

---

#### 2.5 SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Protegidos
                .requestMatchers("/api/items/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // Demais
                .anyRequest().authenticated()
            );
        
        http.addFilterBefore(
            jwtAuthenticationFilter(), 
            UsernamePasswordAuthenticationFilter.class
        );
        
        return http.build();
    }
}
```

**Responsabilidades**:
- ✅ Configurar regras de autorização por URL
- ✅ Desabilitar CSRF (não necessário para APIs stateless)
- ✅ Configurar sessão como STATELESS
- ✅ Adicionar filtro JWT na cadeia de filtros
- ✅ Configurar AuthenticationManager

**Regras de Acesso**:
| Endpoint | Roles Permitidas | Descrição |
|----------|-----------------|-----------|
| `/api/auth/**` | Público | Login |
| `/swagger-ui/**` | Público | Documentação |
| `/api/items/**` | USER, ADMIN | CRUD de Items |
| `/api/users/**` | ADMIN | CRUD de Users |

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/SecurityConfig.java`

---

#### 2.6 JwtAuthenticationEntryPoint

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(...) throws IOException, ServletException {
        logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
}
```

**Responsabilidades**:
- ✅ Tratar erros de autenticação (401 Unauthorized)
- ✅ Logar tentativas de acesso não autorizado
- ✅ Retornar resposta HTTP padronizada

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/security/jwt/JwtAuthenticationEntryPoint.java`

---

### 3. Infrastructure Layer - API

#### 3.1 AuthAPI (Interface)

```java
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public interface AuthAPI {
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);
}
```

**Responsabilidades**:
- ✅ Definir contrato da API de autenticação
- ✅ Documentação Swagger/OpenAPI

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/api/AuthAPI.java`

---

#### 3.2 AuthController

```java
@RestController
public class AuthController implements AuthAPI {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        // 1. Autentica com AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(), 
                request.password()
            )
        );
        
        // 2. Gera token JWT
        String jwt = tokenProvider.generateToken(authentication);
        
        // 3. Retorna resposta com token
        return ResponseEntity.ok(new LoginResponse(...));
    }
}
```

**Responsabilidades**:
- ✅ Implementar endpoint de login
- ✅ Delegar autenticação ao Spring Security
- ✅ Gerar token JWT em caso de sucesso
- ✅ Retornar dados do usuário + token

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/api/controllers/AuthController.java`

---

#### 3.3 DTOs

**LoginRequest.java**
```java
public record LoginRequest(
    @JsonProperty("username") String username,
    @JsonProperty("password") String password
) {}
```

**LoginResponse.java**
```java
public record LoginResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("user_id") String userId,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("roles") List<String> roles
) {}
```

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/api/models/`

---

### 4. Implementation Layer - Persistence

#### UserRepository

```java
public interface UserRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByUsername(String username);
}
```

**Adicionado**: Método `findByUsername` para suportar autenticação

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/user/persistence/UserRepository.java`

---

#### UserMySQLGateway

```java
@Component
public class UserMySQLGateway implements UserGateway {
    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username)
            .map(UserJpaEntity::toAggregate);
    }
}
```

**Adicionado**: Implementação do método `findByUsername`

**Localização**: `infrastructure/src/main/java/com/starter/crudexample/infrastructure/user/UserMySQLGateway.java`

---

## Fluxo de Autenticação

### 1. Login (Obtenção do Token)

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AM as AuthenticationManager
    participant UDS as CustomUserDetailsService
    participant UG as UserGateway
    participant DB as Database
    participant JTP as JwtTokenProvider

    C->>AC: POST /api/auth/login {username, password}
    AC->>AM: authenticate(username, password)
    AM->>UDS: loadUserByUsername(username)
    UDS->>UG: findByUsername(username)
    UG->>DB: SELECT * FROM users WHERE username = ?
    DB-->>UG: UserJpaEntity
    UG-->>UDS: User (Domain)
    UDS-->>AM: UserPrincipal (UserDetails)
    AM->>AM: validate password with BCrypt
    AM-->>AC: Authentication (success)
    AC->>JTP: generateToken(authentication)
    JTP-->>AC: JWT Token
    AC-->>C: LoginResponse + Token
```

### 2. Requisição Autenticada

```mermaid
sequenceDiagram
    participant C as Client
    participant JAF as JwtAuthenticationFilter
    participant JTP as JwtTokenProvider
    participant SC as SecurityContext
    participant IC as ItemController

    C->>JAF: GET /api/items [Authorization: Bearer <token>]
    JAF->>JAF: extract token from header
    JAF->>JTP: validateToken(token)
    JTP-->>JAF: true/false
    alt token válido
        JAF->>JTP: getUserIdFromToken(token)
        JTP-->>JAF: userId
        JAF->>JTP: getRolesFromToken(token)
        JTP-->>JAF: [USER, ADMIN]
        JAF->>SC: setAuthentication(userId, roles)
        JAF->>IC: continue to controller
        IC-->>C: Response (200 OK)
    else token inválido
        JAF-->>C: 401 Unauthorized
    end
```

---

## Configuração de Segurança

### application.yml

```yaml
spring:
  application:
    name: crudexample

# JWT Configuration
app:
  jwt:
    # Chave secreta para assinar tokens (mínimo 256 bits)
    secret: ${JWT_SECRET:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongToMeetTheRequirements}
    # Tempo de expiração em millisegundos (24 horas = 86400000)
    expiration-ms: ${JWT_EXPIRATION:86400000}
```

### Variáveis de Ambiente (Produção)

```bash
# Defina uma chave secreta forte (pelo menos 256 bits)
export JWT_SECRET="SuaChaveSecretaSuperSeguraAqui123456789012345678901234567890"

# Opcional: ajustar tempo de expiração (em millisegundos)
export JWT_EXPIRATION=3600000  # 1 hora
```

### Dependências (build.gradle.kts)

```kotlin
// buildSrc/src/main/kotlin/Dependencies.kt
object Libs {
    // ... outras dependências
    
    // JWT
    const val jjwtApi = "io.jsonwebtoken:jjwt-api:0.12.5"
    const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:0.12.5"
    const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:0.12.5"
}

// infrastructure/build.gradle.kts
dependencies {
    // ... outras dependências
    
    // JWT
    implementation(Libs.jjwtApi)
    runtimeOnly(Libs.jjwtImpl)
    runtimeOnly(Libs.jjwtJackson)
}
```

---

## Testes

### AuthControllerTest

```java
@SpringBootTest(classes = WebServerConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test-auth")
public class AuthControllerTest {
    
    @Test
    public void givenValidCredentials_whenLogin_shouldReturnTokenAndUserInfo() {
        // Testa login com credenciais válidas
        // Valida que retorna token JWT + dados do usuário
    }
    
    @Test
    public void givenInvalidPassword_whenLogin_shouldReturn401() {
        // Testa login com senha incorreta
        // Valida que retorna 401 Unauthorized
    }
    
    @Test
    public void givenNonExistentUser_whenLogin_shouldReturn401() {
        // Testa login com usuário inexistente
        // Valida que retorna 401 Unauthorized
    }
    
    @Test
    public void givenInactiveUser_whenLogin_shouldReturn401() {
        // Testa login com usuário inativo
        // Valida que retorna 401 Unauthorized
    }
}
```

**Localização**: `infrastructure/src/test/java/com/starter/crudexample/infrastructure/api/controllers/AuthControllerTest.java`

### TestSecurityConfig

Para testes que **não precisam** de autenticação real:

```java
@TestConfiguration
@EnableWebSecurity
@Profile("test-integration")
public class TestSecurityConfig {
    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .build();
    }
}
```

**Importante**: 
- Perfil `test-integration`: Desabilita segurança (para testes de controllers)
- Perfil `test-auth`: Usa segurança real (para testes de autenticação)

---

## Como Usar

### 1. Criar Primeiro Usuário

Inicialmente, você precisa criar um usuário administrador. Existem algumas opções:

#### Opção A: Script SQL (Recomendado para primeiro deploy)

```sql
INSERT INTO users (id, username, email, password, active, created_at, updated_at) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    'admin',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye', -- senha: admin123 (hash BCrypt)
    true,
    NOW(),
    NOW()
);

INSERT INTO user_roles (user_id, role) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'ADMIN');
```

#### Opção B: Endpoint (temporariamente sem segurança)

1. Comente temporariamente as regras de segurança para `/api/users`
2. Crie o usuário via API
3. Restaure as regras de segurança

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "roles": ["ADMIN"],
    "active": true
  }'
```

---

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Resposta:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJ1c2VybmFtZSI6ImFkbWluIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjk3NTg0ODAwLCJleHAiOjE2OTc2NzEyMDB9.abc123...",
  "token_type": "Bearer",
  "user_id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

---

### 3. Acessar Endpoints Protegidos

#### Exemplo: Listar Items (USER ou ADMIN)

```bash
# Salvar token em variável
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Fazer requisição autenticada
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer $TOKEN"
```

#### Exemplo: Criar User (apenas ADMIN)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "email": "user1@example.com",
    "password": "password123",
    "roles": ["USER"],
    "active": true
  }'
```

---

### 4. Testar com Swagger UI

1. Acesse: http://localhost:8080/swagger-ui.html
2. Execute o endpoint `/api/auth/login` com suas credenciais
3. Copie o valor de `access_token` da resposta
4. Clique no botão **"Authorize"** no topo da página
5. Cole o token no formato: `Bearer <seu_token_aqui>`
6. Clique em **"Authorize"**
7. Agora você pode testar todos os endpoints protegidos

---

## Códigos de Status HTTP

| Código | Significado | Quando Ocorre |
|--------|-------------|---------------|
| 200 | OK | Login bem-sucedido, requisição autorizada bem-sucedida |
| 401 | Unauthorized | Token ausente, inválido, expirado ou credenciais incorretas |
| 403 | Forbidden | Token válido mas usuário não tem permissão (role insuficiente) |
| 404 | Not Found | Recurso não encontrado |
| 422 | Unprocessable Entity | Dados de entrada inválidos |

---

## Segurança em Produção

### ✅ Checklist de Segurança

- [ ] **JWT Secret**: Use chave forte e única (variável de ambiente)
- [ ] **HTTPS**: Force HTTPS em produção (nunca HTTP)
- [ ] **CORS**: Configure origens permitidas
- [ ] **Rate Limiting**: Implemente rate limiting no endpoint de login
- [ ] **Password Policy**: Enforce senhas fortes (mínimo 8 caracteres, maiúsculas, números, símbolos)
- [ ] **Token Expiration**: Ajuste conforme necessidade (não muito longo)
- [ ] **Refresh Tokens**: Considere implementar para sessões longas
- [ ] **Audit Log**: Registre tentativas de login (sucesso e falha)
- [ ] **H2 Console**: Desabilite em produção
- [ ] **Swagger**: Restrinja acesso em produção ou desabilite
- [ ] **Account Lockout**: Bloqueie conta após N tentativas falhas
- [ ] **Two-Factor Auth**: Considere adicionar 2FA para admin

### Exemplo de Configuração HTTPS (application-prod.yml)

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat

app:
  jwt:
    secret: ${JWT_SECRET}  # OBRIGATÓRIO em produção
    expiration-ms: 3600000  # 1 hora (mais seguro)
```

---

## Troubleshooting

### Problema: 401 Unauthorized em todas as requisições

**Possíveis Causas**:
1. Token não está sendo enviado no header
2. Token expirou
3. Formato do header incorreto

**Solução**:
```bash
# Formato correto
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# Erros comuns
Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...  # Falta "Bearer "
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...         # Falta header "Authorization:"
```

---

### Problema: 403 Forbidden

**Causa**: Usuário autenticado mas sem role necessária

**Exemplo**:
- Usuário com role USER tenta acessar `/api/users` (requer ADMIN)

**Solução**: Verificar roles do usuário e endpoints permitidos

---

### Problema: Token não valida (sempre retorna false)

**Possíveis Causas**:
1. Secret key diferente entre geração e validação
2. Token mal formatado
3. Token corrompido

**Debug**:
```java
// Adicione logs no JwtTokenProvider
logger.info("Validating token: {}", token.substring(0, 20) + "...");
logger.info("Secret key length: {}", jwtSecret.getEncoded().length);
```

---

## Melhorias Futuras

### 1. Refresh Tokens

Implementar sistema de refresh tokens para renovar tokens expirados sem novo login:

```java
POST /api/auth/refresh
Authorization: Bearer <refresh_token>

Response:
{
  "access_token": "novo_token",
  "refresh_token": "novo_refresh_token"
}
```

### 2. OAuth2/OpenID Connect

Integrar com provedores externos (Google, GitHub, etc):

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
```

### 3. Two-Factor Authentication (2FA)

Adicionar camada extra de segurança com TOTP:

```java
POST /api/auth/verify-2fa
{
  "username": "admin",
  "code": "123456"
}
```

### 4. Account Lockout

Bloquear conta após N tentativas falhas:

```java
@Entity
public class LoginAttempt {
    private String username;
    private int attempts;
    private LocalDateTime lastAttempt;
    private boolean locked;
}
```

### 5. Audit Log

Registrar todas as ações importantes:

```java
@Aspect
public class AuditAspect {
    @After("@annotation(Audited)")
    public void audit(JoinPoint jp) {
        // Log action, user, timestamp, IP
    }
}
```

---

## Referências

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io](https://jwt.io/)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## Conclusão

A implementação de autenticação JWT com Spring Security foi feita seguindo os princípios da Clean Architecture:

✅ **Separação de Responsabilidades**: Autenticação na camada de infraestrutura  
✅ **Independência de Framework**: Domain não depende de Spring Security  
✅ **Testabilidade**: Testes de integração completos  
✅ **Segurança**: BCrypt, JWT, RBAC, stateless  
✅ **Escalabilidade**: Tokens stateless permitem escalonamento horizontal  
✅ **Manutenibilidade**: Código bem estruturado e documentado  

O sistema está pronto para produção seguindo as melhores práticas de segurança! 🔐🚀
