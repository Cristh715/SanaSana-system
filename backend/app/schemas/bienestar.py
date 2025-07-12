from pydantic import BaseModel, Field, validator
from typing import List, Optional
from datetime import datetime
import json 

class RegistroBienestarBase(BaseModel):
    estado_emocional: str = Field(..., max_length=50, example="Contento")
    sintomas_fisicos: Optional[List[str]] = Field(None, example=["Dolor de cabeza", "Cansancio"])
    nota_adicional: Optional[str] = Field(None, example="Me siento un poco cansado hoy.")

class RegistroBienestarCreate(RegistroBienestarBase):
    pass

class RegistroBienestarResponse(RegistroBienestarBase):
    id_registro_bienestar: int
    id_paciente: int
    fecha_registro: datetime

    @validator('sintomas_fisicos', pre=True)
    def parse_sintomas_fisicos(cls, v):
        if isinstance(v, str):
            try:
                return json.loads(v)
            except json.JSONDecodeError:
                raise ValueError("sintomas_fisicos must be a valid JSON array string or a list")
        return v 

    class Config:
        from_attributes = True