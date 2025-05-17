from pydantic import BaseModel, EmailStr
from datetime import date

class Paciente(BaseModel):
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