import uvicorn
from fastapi import FastAPI
from app.routers import register_router
from app.routers.cita_router import cita_router

app = FastAPI(title="SanaSana API")
app.include_router(register_router)
app.include_router(cita_router)

if __name__ == "__main__":
    uvicorn.run("app.main:app", host="127.0.0.1", port=8000, reload=True)
