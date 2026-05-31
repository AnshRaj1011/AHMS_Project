"""
AHMS Chatbot Microservice
Run: pip install fastapi uvicorn
Start: uvicorn main:app --port 5002
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import re

app = FastAPI(title="AHMS Chatbot Service")
app.add_middleware(CORSMiddleware, allow_origins=["*"],
                   allow_methods=["*"], allow_headers=["*"])

# ─── Intent → patterns → response ────────────────────────────────────────────
INTENTS = [
    {
        "intent": "greeting",
        "patterns": ["hello", "hi", "hey", "good morning", "good evening", "namaste", "hii"],
        "response": "Hello! 👋 I'm AHMS Assistant. I can help you with:\n• Book appointments\n• Check bed availability\n• Request ambulance\n• Lab test booking\n• ICU emergency requests\n• Find doctors\n\nHow can I help you today?"
    },
    {
        "intent": "appointment_book",
        "patterns": ["book appointment", "schedule appointment", "see doctor", "consult doctor",
                     "appointment book", "doctor appointment", "visit doctor"],
        "response": "To book an appointment:\n1. Go to **Appointments** section\n2. Select a doctor by specialization\n3. Choose a date and time slot\n4. Confirm your booking\n\nYour appointment will be confirmed shortly. Need help finding a specific doctor?"
    },
    {
        "intent": "appointment_cancel",
        "patterns": ["cancel appointment", "reschedule", "appointment cancel"],
        "response": "To cancel or reschedule an appointment:\n1. Go to **My Appointments**\n2. Select the appointment\n3. Click **Cancel** or **Reschedule**\n\nPlease cancel at least 2 hours in advance."
    },
    {
        "intent": "bed_availability",
        "patterns": ["bed available", "bed availability", "available beds", "bed status",
                     "ward bed", "any bed", "room available", "admission"],
        "response": "To check bed availability:\n1. Go to **Beds** section\n2. Filter by ward type (General / ICU / Private)\n\nBed types available:\n• General Ward\n• ICU\n• Private Room\n• Semi-Private\n• Emergency"
    },
    {
        "intent": "icu_request",
        "patterns": ["icu", "intensive care", "critical", "emergency bed", "icu bed",
                     "icu request", "critical care"],
        "response": "🚨 For ICU Emergency:\n1. Go to **ICU Emergency** section\n2. Fill in the patient details and medical condition\n3. Select priority level\n4. Submit request\n\nOur team will review and respond immediately. For life-threatening emergencies, also call **112**."
    },
    {
        "intent": "ambulance",
        "patterns": ["ambulance", "emergency", "help urgent", "urgent help", "need ambulance",
                     "call ambulance", "accident"],
        "response": "🚑 To request an ambulance:\n1. Go to **Emergency > Request Ambulance**\n2. Enter your location or share GPS\n3. Describe the emergency\n4. Submit — our team dispatches immediately\n\nFor immediate help, also call **108** (National Ambulance)."
    },
    {
        "intent": "lab_test",
        "patterns": ["lab test", "blood test", "mri", "ct scan", "x-ray", "xray", "urine test",
                     "ecg", "ultrasound", "diagnostic", "test booking", "book test"],
        "response": "To book a lab test:\n1. Go to **Lab Tests** section\n2. Select test type:\n   • Blood Test (₹300)\n   • CT Scan (₹3500)\n   • MRI (₹6000)\n   • X-Ray (₹500)\n   • ECG (₹400)\n   • Ultrasound (₹1200)\n3. Choose date and time\n4. Confirm booking\n\nReports will be uploaded once ready."
    },
    {
        "intent": "find_doctor",
        "patterns": ["find doctor", "doctor list", "which doctor", "specialist", "cardiologist",
                     "neurologist", "orthopedic", "pediatrician", "gynecologist", "surgeon"],
        "response": "To find a doctor:\n1. Go to **Doctors** section\n2. Search by name or specialization\n3. View available time slots\n4. Book directly\n\nSpecializations available:\n• Cardiology\n• Neurology\n• Orthopedics\n• Oncology\n• Nephrology\n• Gastroenterology\n• Pediatrics and more"
    },
    {
        "intent": "consultation",
        "patterns": ["online consultation", "virtual", "video call", "telemedicine",
                     "live consultation", "consult online", "remote"],
        "response": "For virtual consultation:\n1. Go to **Appointments**\n2. Select **Virtual** as appointment type\n3. Choose doctor and time slot\n4. Join via the **Live Chat** section at the scheduled time\n\nYou can chat with your doctor in real time."
    },
    {
        "intent": "registration",
        "patterns": ["register", "new patient", "create account", "sign up", "registration"],
        "response": "To register as a new patient:\n1. Click **Register** on the home page\n2. Fill in your details\n3. A unique Registration ID (e.g. AHMS-2024-00001) will be assigned\n4. A QR code will be generated for quick identification inside the hospital"
    },
    {
        "intent": "qr_code",
        "patterns": ["qr code", "patient qr", "identification", "patient id", "registration id"],
        "response": "Your QR code is generated automatically when you register. It contains your Registration ID and name for quick identification inside the hospital.\n\nTo download your QR:\n1. Go to **My Profile**\n2. Click **Download QR Code**"
    },
    {
        "intent": "hospital_map",
        "patterns": ["map", "navigation", "where is", "location", "find department",
                     "how to reach", "floor", "direction"],
        "response": "🗺️ Hospital Navigation:\n• **Ground Floor:** Emergency, Ambulance Bay, Reception\n• **1st Floor:** General OPD, Pharmacy\n• **2nd Floor:** ICU, Cardiology, Neurology\n• **3rd Floor:** Orthopedics, Surgery\n• **4th Floor:** Laboratory, Radiology (MRI/CT/X-Ray)\n\nGo to **Hospital Map** section for interactive navigation."
    },
    {
        "intent": "billing",
        "patterns": ["bill", "payment", "charges", "fees", "cost", "price", "invoice"],
        "response": "For billing and payments:\n1. Go to **Billing** section in your dashboard\n2. View itemized bill\n3. Pay online or at the billing counter\n\nFor queries, contact the Billing Desk at the hospital reception."
    },
    {
        "intent": "emergency",
        "patterns": ["emergency", "urgent", "critical patient", "help", "sos"],
        "response": "🚨 EMERGENCY CONTACTS:\n• **Ambulance:** 108\n• **Police:** 100\n• **Fire:** 101\n• **Hospital Emergency:** 112\n\nThrough AHMS:\n• Request Ambulance\n• Submit ICU Emergency Request\n• Contact Emergency Staff\n\nPlease act immediately for life-threatening situations."
    },
    {
        "intent": "goodbye",
        "patterns": ["bye", "goodbye", "thanks", "thank you", "ok bye", "that's all", "done"],
        "response": "Thank you for using AHMS! 🏥 Stay healthy. If you need help anytime, I'm here. Take care! 😊"
    },
]

# ─── Matcher ──────────────────────────────────────────────────────────────────
def get_intent(message: str) -> str | None:
    msg = message.lower().strip()
    for item in INTENTS:
        for pattern in item["patterns"]:
            if pattern in msg:
                return item["response"]
    return None

def keyword_score(message: str) -> str:
    """Fallback: score each intent by number of matching words."""
    msg_words = set(re.findall(r'\w+', message.lower()))
    best_score = 0
    best_response = None
    for item in INTENTS:
        score = sum(1 for p in item["patterns"] if any(w in msg_words for w in p.split()))
        if score > best_score:
            best_score = score
            best_response = item["response"]
    return best_response

DEFAULT_RESPONSE = (
    "I'm not sure I understood that. Here's what I can help with:\n"
    "• 📅 Book/cancel appointments\n"
    "• 🛏️ Check bed availability\n"
    "• 🚑 Request ambulance\n"
    "• 🔬 Book lab tests\n"
    "• 🏥 ICU emergency requests\n"
    "• 👨‍⚕️ Find doctors\n"
    "• 🗺️ Hospital navigation\n\n"
    "Please type your question and I'll do my best to help!"
)

# ─── API ──────────────────────────────────────────────────────────────────────
class ChatRequest(BaseModel):
    message: str

class ChatResponse(BaseModel):
    reply: str

@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    reply = get_intent(req.message)
    if not reply:
        reply = keyword_score(req.message)
    if not reply:
        reply = DEFAULT_RESPONSE
    return ChatResponse(reply=reply)

@app.get("/health")
def health():
    return {"status": "Chatbot service running", "intents": len(INTENTS)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5002)
