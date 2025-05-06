# taskolotl-backend

Copy code related to an entity for generating tests and improvement recommendations:

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

Add the following VM arg to handle the Mockito agent warning:

`-XX:+EnableDynamicAgentLoading`

Versioning:

- fix: → Patch release (1.0.0 → 1.0.1)
- feat: → Minor release (1.0.0 → 1.1.0)
- Any commit with BREAKING CHANGE: → Major release (1.0.0 → 2.0.0)