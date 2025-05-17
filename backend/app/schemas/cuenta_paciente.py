from pydantic import BaseModel, EmailStr, ConfigDict
from datetime import datetime

class CuentaPaciente(BaseModel):
    id_cuenta: int
    correo: EmailStr
    contrasena_hash: str
    cuenta_bloqueada: bool
    fecha_registro: datetime
