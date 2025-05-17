import jwt
from datetime import datetime, timedelta
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jwt import PyJWTError  # Usando pyjwt error para manejo de excepciones
from app.utils.settings import JWT_SECRET, JWT_ALGORITHM, JWT_EXPIRE_MINUTES

JWT_SECRET = os.getenv("JWT_SECRET")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
JWT_EXPIRE_MINUTES = int(os.getenv("JWT_EXPIRE_MINUTES", 60))


from fastapi import Request

if os.getenv("TEST_MODE") == "1":
    async def fake_oauth2_scheme(request: Request):
        return ""
    oauth2_scheme = fake_oauth2_scheme
else:
    oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

def create_access_token(data: dict) -> str:
    """
    Genera un JWT con payload en `data` y expiración configurada.
    """
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=JWT_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, JWT_SECRET, algorithm=JWT_ALGORITHM)

async def get_current_user_id(token: str = Depends(oauth2_scheme)) -> int:
    """
    Decodifica el token JWT y extrae el user_id del payload 'sub'.
    Si está activado el modo test (TEST_MODE=1), retorna un ID simulado.
    """
    # 👉 Modo test activo, retorna un user_id fijo
    if os.getenv("TEST_MODE") == "1":
        print("⚠️ Modo test activo: usando user_id simulado")
        return 1  # Puedes cambiar el ID si quieres probar con otro

    # ⚠️ Modo normal con JWT
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