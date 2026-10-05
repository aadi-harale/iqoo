import type { Locale } from "./domain";

/**
 * On-device intake, for the iQOO build.
 *
 * Nothing here touches the network. The member speaks or types, the handset turns that into a
 * scoped work ticket, and the cooperative keeps the recording. This is the same boundary the
 * cloud routes carry in their system prompts, enforced here by having no capability at all:
 * this module can describe a complaint. It cannot choose a worker, price a job, rank a member,
 * decide an appeal or change a policy.
 */

export type Urgency = "urgent" | "standard";

export type OnDeviceIntake = {
  /** Always "on-device" — this path never leaves the handset. */
  mode: "on-device";
  summary: string;
  suggestedService: string;
  /** Empty when nothing in the text points at a trade; we never guess silently. */
  detectedFrom: string[];
  urgency: Urgency;
  safetyNote: string;
  scopeQuestions: string[];
  notice: string;
};

/** Trade keywords in all three languages the member may speak. */
const SERVICE_WORDS: Record<string, string[]> = {
  electrician: ["switch", "socket", "wiring", "light", "bulb", "fan", "spark", "shock", "meter", "mcb",
    "स्विच", "सॉकेट", "वायरिंग", "बत्ती", "पंखा", "मीटर", "करंट", "ठिणगी", "दिवा"],
  plumbing: ["leak", "tap", "pipe", "drain", "flush", "water", "blockage", "cistern",
    "नळ", "नल", "पाइप", "पाईप", "गळती", "पाणी", "पानी", "ड्रेन", "तुंबल"],
  cleaning: ["clean", "sweep", "mop", "dust", "stain", "rubbish",
    "सफाई", "साफ", "स्वच्छता", "झाडू", "कचरा"],
  carpentry: ["door", "hinge", "cupboard", "drawer", "shelf", "wood", "lock",
    "दरवाजा", "दरवाजे", "कपाट", "बिजागरी", "कुलूप", "लाकूड"],
  appliance: ["fridge", "refrigerator", "washing machine", "ac", "air conditioner", "geyser", "oven",
    "फ्रिज", "फ्रीज", "वॉशिंग", "मशीन", "गिझर", "गीजर", "एसी"],
};

/** Words that mean "this may hurt someone", in all three languages. */
const DANGER_WORDS = ["spark", "smoke", "burn", "burning", "fire", "shock", "flood", "gas", "danger",
  "urgent", "emergency", "leaking badly",
  "ठिणगी", "धूर", "धुआं", "आग", "जळ", "जल", "करंट", "झटका", "पूर", "गॅस", "गैस", "तातडी", "तुरंत", "धोका", "खतरा"];

const COPY = {
  en: {
    safeUrgent: "If anyone is in danger, switch off the supply only if it is safe to reach, move away, and call emergency services.",
    safeStandard: "Keep the area clear so the member can work, and point out any visible damage when they arrive.",
    q1: "What exactly is not working?",
    q2: "When did it start?",
    q3: "Is anything still live, leaking or unsafe?",
    notice: "Structured on this phone. Nothing was sent to a server. The cooperative — not this handset — decides who takes the job, what it pays and how any complaint ends.",
    fallbackSummary: "service request",
  },
  hi: {
    safeUrgent: "अगर किसी को खतरा है, तो सप्लाई तभी बंद करें जब वहाँ तक पहुँचना सुरक्षित हो, दूर हट जाएँ, और आपातकालीन सेवा को बुलाएँ।",
    safeStandard: "जगह खाली रखें ताकि सदस्य काम कर सके, और आने पर दिखने वाला कोई भी नुकसान बता दें।",
    q1: "ठीक-ठीक क्या काम नहीं कर रहा है?",
    q2: "यह कब से शुरू हुआ?",
    q3: "क्या अब भी कहीं करंट, रिसाव या खतरा बाकी है?",
    notice: "इसी फ़ोन पर तैयार किया गया। कुछ भी सर्वर पर नहीं भेजा गया। काम किसे मिलेगा, कितना पैसा मिलेगा और शिकायत का क्या नतीजा होगा — यह सहकारी समिति तय करती है, यह फ़ोन नहीं।",
    fallbackSummary: "सेवा अनुरोध",
  },
  mr: {
    safeUrgent: "कोणाला धोका असेल, तर तिथवर पोहोचणे सुरक्षित असेल तरच पुरवठा बंद करा, लांब व्हा आणि आपत्कालीन सेवेला बोलवा.",
    safeStandard: "सदस्याला काम करता यावे म्हणून जागा मोकळी ठेवा, आणि ते आल्यावर दिसणारे नुकसान दाखवा.",
    q1: "नेमके काय चालत नाही?",
    q2: "हे कधीपासून सुरू झाले?",
    q3: "अजूनही कुठे करंट, गळती किंवा धोका आहे का?",
    notice: "याच फोनवर तयार केले. काहीही सर्व्हरवर पाठवले गेले नाही. काम कोणाला मिळणार, किती पैसे मिळणार आणि तक्रारीचे काय होणार — हे सहकारी संस्था ठरवते, हा फोन नाही.",
    fallbackSummary: "सेवा विनंती",
  },
} as const;

function hit(lower: string, words: string[]): string[] {
  return words.filter((word) => lower.includes(word));
}

/**
 * Turn what the member said into a scoped ticket, on the handset.
 *
 * Deterministic: the same text always produces the same ticket, which is what lets the
 * cooperative replay an intake the same way it replays an allocation.
 */
export function parseOnDevice(service: string, text: string, locale: Locale = "en"): OnDeviceIntake {
  const copy = COPY[locale] ?? COPY.en;
  const trimmed = text.trim();
  const lower = trimmed.toLowerCase();

  const dangerHits = hit(lower, DANGER_WORDS);
  const urgency: Urgency = dangerHits.length > 0 ? "urgent" : "standard";

  // Only override the chosen service when the member's own words point somewhere specific.
  let suggestedService = service;
  let detectedFrom: string[] = [];
  let best = 0;
  for (const [trade, words] of Object.entries(SERVICE_WORDS)) {
    const hits = hit(lower, words);
    if (hits.length > best) { best = hits.length; suggestedService = trade; detectedFrom = hits; }
  }

  const questions: string[] = [copy.q1, copy.q2];
  if (urgency === "urgent") questions.push(copy.q3);

  return {
    mode: "on-device",
    summary: trimmed.slice(0, 180) || `${service} ${copy.fallbackSummary}`,
    suggestedService,
    detectedFrom,
    urgency,
    safetyNote: urgency === "urgent" ? copy.safeUrgent : copy.safeStandard,
    scopeQuestions: questions,
    notice: copy.notice,
  };
}

/** What this handset can actually do, read at runtime so the UI never promises more than it has. */
export type DeviceCapabilities = {
  speechToText: boolean;
  readAloud: boolean;
  camera: boolean;
  offlineReady: boolean;
};

export function readCapabilities(): DeviceCapabilities {
  if (typeof window === "undefined") {
    return { speechToText: false, readAloud: false, camera: false, offlineReady: false };
  }
  const w = window as unknown as Record<string, unknown>;
  return {
    speechToText: Boolean(w.SpeechRecognition || w.webkitSpeechRecognition),
    readAloud: typeof window.speechSynthesis !== "undefined",
    camera: Boolean(navigator.mediaDevices?.getUserMedia),
    offlineReady: true, // parseOnDevice has no network path at all
  };
}

/** BCP-47 tags for the speech APIs, so a member is heard and answered in their own language. */
export const SPEECH_TAG: Record<Locale, string> = { en: "en-IN", hi: "hi-IN", mr: "mr-IN" };
