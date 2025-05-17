from pydantic import BaseModel, EmailStr, ConfigDict
from datetime import date

class PacienteCreate(BaseModel):
    dni: str
    digitoVerificador: str
    nombres: str
    apellidoPaterno: str
    apellidoMaterno: str
    correo: EmailStr
    telefono: str
    password: str
    password_confirm: str
    fecha_nacimiento: date

class TokenResponse(BaseModel):
    access_token: str
    token_type: str

    model_config = ConfigDict(from_attributes=True)
