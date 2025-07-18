# taskolotl-backend

## API Docs

- http://localhost:8080/taskolotl/swagger-ui/index.html
- http://localhost:8080/taskolotl/v3/api-docs

## Generate a Bearer Token

- http://localhost:8080/taskolotl/login/oauth2/code/google

## Code Analysis

Useful for troubleshooting, improving, documenting, and testing with coding assistants

```bash
# Base directory of your project
BASE_DIR="src/main/java/com/saunderscox/taskolotl"
ENTITY="User"

# Concatenate all the files and copy to clipboard
cat "$BASE_DIR/entity/${ENTITY}.java" \
    "$BASE_DIR/dto/${ENTITY}UpdateRequestDto.java" \
    "$BASE_DIR/dto/${ENTITY}CreateRequestDto.java" \
    "$BASE_DIR/dto/${ENTITY}ResponseDto.java" \
    "$BASE_DIR/dto/${ENTITY}UpdateRequestDto.java" \
    "$BASE_DIR/mapper/${ENTITY}Mapper.java" | clip

echo "Contents of ${ENTITY} files copied to clipboard!"
```

```bash
# Base directory of your project
BASE_DIR="src/main/java/com/saunderscox/taskolotl"

# Concatenate all security files and copy to clipboard
cat \
"$BASE_DIR/config/security/SecurityConfig.java" \
"$BASE_DIR/config/security/OAuth2SuccessHandler.java" \
"$BASE_DIR/service/AuthService.java" \
"$BASE_DIR/controller/AuthController.java" \
| clip

echo "Contents of security files copied to clipboard!"
```

## Versioning

- fix: → Patch release (1.0.0 → 1.0.1)
- feat: → Minor release (1.0.0 → 1.1.0)
- Any commit with BREAKING CHANGE: → Major release (1.0.0 → 2.0.0)
- See https://github.com/semantic-release/semantic-release?tab=readme-ov-file#how-does-it-work

## Setup

### Disable Mockito Warning

Add the following VM arg to handle the Mockito agent warning:

`-XX:+EnableDynamicAgentLoading`

## Configuration
