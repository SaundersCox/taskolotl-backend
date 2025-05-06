# taskolotl-backend

Copy code related to an entity for generating tests:
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