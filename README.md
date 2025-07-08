# Flashy - Flashcard Study App 📚⚡

Flashy is a modern flashcard app with TikTok-style infinite scrolling and optional AI-generated flashcards using HuggingFace models.

## ✨ Features

- Add flashcards manually
- Generate quiz Q&A with HuggingFace AI
- Local SQLite storage
- Clean UI with dropdown for number of questions

---

## 🔧 Setup

### 1. Clone the repo

```bash
git clone https://github.com/your-username/flashy.git
cd flashy
```

### 2. Generate HuggingFace API key

- Sign up at: https://huggingface.co/
- Go to: https://huggingface.co/settings/tokens → generate a new token

### 3. Add API key to gradle.properties

```
# Hugging Face API Key
HUGGINGFACE_API_KEY=YOURAPIKEY
```

**⚠️ DO NOT COMMIT `gradle.properties` WITH YOUR API KEY.**  
Add it to your `.gitignore` if needed.

---

## ▶️ Run the App

- Open the project in Android Studio
- Plug in a device or run an emulator
- Click **Run**

---

## 🧠 AI Usage

AI flashcard generation is powered by HuggingFace model `llama-v3p1-8b-instruct`.  
You can customize the model in your Kotlin code and control how many questions are generated using the dropdown next to the topic input.
