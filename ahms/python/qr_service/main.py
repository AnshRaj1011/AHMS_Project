"""
AHMS QR Code Microservice
Run: pip install fastapi uvicorn qrcode pillow
Start: uvicorn main:app --port 5001
"""
import os
import qrcode
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel

app = FastAPI(title="AHMS QR Service")

QR_DIR = "qr_codes"
os.makedirs(QR_DIR, exist_ok=True)

class QrRequest(BaseModel):
    registration_id: str
    patient_name: str

@app.post("/generate-qr")
def generate_qr(req: QrRequest):
    """Generate a QR code for a patient registration ID."""
    content = f"AHMS Patient\nName: {req.patient_name}\nID: {req.registration_id}"
    
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_H,
        box_size=10,
        border=4,
    )
    qr.add_data(content)
    qr.make(fit=True)
    
    img = qr.make_image(fill_color="black", back_color="white")
    filename = f"{req.registration_id.replace('-', '_')}.png"
    path = os.path.join(QR_DIR, filename)
    img.save(path)
    
    return {"qr_path": path, "registration_id": req.registration_id}

@app.get("/qr-image/{registration_id}")
def get_qr_image(registration_id: str):
    """Return the QR code image file."""
    filename = f"{registration_id.replace('-', '_')}.png"
    path = os.path.join(QR_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(status_code=404, detail="QR code not found")
    return FileResponse(path, media_type="image/png")

@app.get("/health")
def health():
    return {"status": "QR Service running"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5001)
