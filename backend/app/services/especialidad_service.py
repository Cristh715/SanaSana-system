from app.services.predictor import predecir_especialidad

async def obtener_especialidad_desde_texto(sintomas: str):
    resultado = predecir_especialidad(sintomas)
    return resultado