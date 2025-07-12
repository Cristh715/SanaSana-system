from app.core.model_state import pipeline_urgency, pipeline_specialty

def predecir_urgencia(texto: str) -> dict:
    resultado = pipeline_urgency(texto)[0]
    return {
        "clasificacion": resultado["label"],
    }

def predecir_especialidad(texto: str) -> dict:
    resultado = pipeline_specialty(texto)[0]
    return {
        "clasificacion": resultado["label"],
    }
