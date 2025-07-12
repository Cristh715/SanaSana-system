from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline

def load_pipeline(model_path: str):
    tokenizer = AutoTokenizer.from_pretrained(model_path, local_files_only=True)
    model = AutoModelForSequenceClassification.from_pretrained(model_path, local_files_only=True)
    clasificador = pipeline("text-classification", model=model, tokenizer=tokenizer)
    return tokenizer, model, clasificador

# Carga los modelos al iniciar
tokenizer_urgency, model_urgency, pipeline_urgency = load_pipeline("./app/model/urgency_model")
tokenizer_specialty, model_specialty, pipeline_specialty = load_pipeline("./app/model/specialty_model")

# Funciones de predicción
def predecir_urgencia(texto: str) -> dict:
    resultado = pipeline_urgency(texto)[0]
    return {"clasificacion": resultado["label"]}

def predecir_especialidad(texto: str) -> dict:
    resultado = pipeline_specialty(texto)[0]
    return {"clasificacion": resultado["label"]}