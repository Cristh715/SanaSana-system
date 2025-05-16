from pydantic import BaseModel, EmailStr, ConfigDict
from datetime import datetime

class CuentaPacienteResponse(BaseModel):
    id_cuenta: int
    correo: EmailStr
    fecha_registro: datetime

    model_config = ConfigDict(from_attributes=True)
