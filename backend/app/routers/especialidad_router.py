from fastapi import APIRouter
from pydantic import BaseModel
from app.services.especialidad_service import obtener_especialidad_desde_texto

router = APIRouter(prefix="/api/especialidad", tags=["especialidad"])

class TextoEntrada(BaseModel):
    sintomas: str

@router.post("/", summary="Obtener especialidad médica sugerida")
async def obtener_especialidad(data: TextoEntrada):
    resultado = await obtener_especialidad_desde_texto(data.sintomas)
    return resultado
