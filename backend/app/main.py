import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.routers import register_router, disponibilidad_router
from app.routers.cita_router import cita_router
from app.routers import auth
from app.routers import disponibilidad

# App instance
app = FastAPI(
    title="SanaSana System",
    description="SanaSana System API",
    version="0.1.0"
)

# CORS configuration for development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Root endpoint
@app.get("/")
async def root():
    return JSONResponse(content={"message": "Welcome to the SanaSana System API!"}, status_code=200)

# Health check endpoint
@app.get("/health")
async def health_check():
    return JSONResponse(content={"status": "healthy"}, status_code=200)

# Routing
app.include_router(register_router)
app.include_router(disponibilidad_router)
app.include_router(cita_router)
app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(disponibilidad.router)

if __name__ == "__main__":
    uvicorn.run("app.main:app", host="127.0.0.1", port=8000, reload=True)
