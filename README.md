# taskolotl-backend

## Generate a Bearer Token

- http://localhost:8080/taskolotl/login/oauth2/code/google

## Code Analysis

Useful for troubleshooting, improving, documenting, and testing with coding assistants

```
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

## Versioning

- fix: → Patch release (1.0.0 → 1.0.1)
- feat: → Minor release (1.0.0 → 1.1.0)
- Any commit with BREAKING CHANGE: → Major release (1.0.0 → 2.0.0)

## Setup

### Disable Mockito Warning

Add the following VM arg to handle the Mockito agent warning:

`-XX:+EnableDynamicAgentLoading`