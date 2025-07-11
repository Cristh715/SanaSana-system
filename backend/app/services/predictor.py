from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline

def load_model():
    ruta = "./app/model"
    tokenizer = AutoTokenizer.from_pretrained(ruta, local_files_only=True)
    model = AutoModelForSequenceClassification.from_pretrained(ruta, local_files_only=True)
    clasificador = pipeline("text-classification", model=model, tokenizer=tokenizer)
    return tokenizer, model, clasificador

def predict(texto: str, clasificador):
    resultado = clasificador(texto)[0]
    return {
        "texto": texto,
        "clasificacion": resultado["label"],
        "score": round(resultado["score"] * 100, 2)
    }
