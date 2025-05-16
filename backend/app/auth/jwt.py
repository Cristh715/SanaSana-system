import jwt
from datetime import datetime, timedelta
from app.utils.settings import JWT_SECRET, JWT_ALGORITHM, JWT_EXPIRE_MINUTES

def create_access_token(data: dict) -> str:
    """
    Genera un JWT con payload en `data` y expiración configurada.
    """
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=JWT_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, JWT_SECRET, algorithm=JWT_ALGORITHM)
