import jwt
import os
from datetime import datetime, timezone
from datetime import datetime, timedelta
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jwt import PyJWTError  # Usando pyjwt error para manejo de excepciones
from app.utils.settings import JWT_SECRET, JWT_ALGORITHM, JWT_EXPIRE_MINUTES

JWT_SECRET = os.getenv("JWT_SECRET")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
JWT_EXPIRE_MINUTES = int(os.getenv("JWT_EXPIRE_MINUTES", 60))

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

def create_access_token(data: dict) -> str:
    """
    Genera un JWT con payload en `data` y expiración configurada.
    """
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(minutes=JWT_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, JWT_SECRET, algorithm=JWT_ALGORITHM)

async def get_current_user_id(token: str = Depends(oauth2_scheme)) -> int:
    """
    Decodifica el token JWT y extrae el user_id del payload 'sub'.
    """
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="No se pudo validar la credencial",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        user_id = payload.get("sub")
        if user_id is None:
            raise credentials_exception
        return int(user_id)
    except PyJWTError:
        raise credentials_exception