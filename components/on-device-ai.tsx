"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import type { Locale } from "@/lib/domain";
import { parseOnDevice, readCapabilities, SPEECH_TAG, type OnDeviceIntake } from "@/lib/on-device";

/**
 * The phone-first intake surface for the iQOO build.
 *
 * A member who cannot comfortably read or write speaks instead. The handset listens, writes the
 * words down, and turns them into a scoped ticket — without a network call. Where the browser
 * cannot listen (a laptop, a locked-down webview, or a recorded demo), the panel says so plainly
 * and offers a scripted line rather than pretending to hear something.
 */

type Props = {
  locale: Locale;
  service: string;
  /** Called once the member accepts the ticket the handset wrote. */
  onAccept: (intake: OnDeviceIntake) => void;
};

const SAMPLE: Record<Locale, string> = {
  en: "The switch near the kitchen sparks when I turn it on and the light flickers",
  hi: "रसोई के पास वाला स्विच चालू करते ही चिंगारी देता है और बत्ती टिमटिमाती है",
  mr: "स्वयंपाकघराजवळचा स्विच चालू केल्यावर ठिणगी पडते आणि दिवा लुकलुकतो",
};

const LABEL = {
  en: { title: "SPEAK INSTEAD OF TYPING", heading: "Tell us what is wrong", listen: "Hold to speak",
    listening: "Listening…", stop: "Stop", sample: "Use a sample line", clear: "Clear",
    heard: "What the phone heard", ticket: "What the phone wrote", accept: "Use this",
    urgent: "Marked urgent", standard: "Standard", noMic: "This device cannot listen — type, or use a sample line",
    onDevice: "ON DEVICE · NO CLOUD", trade: "Trade detected from" },
  hi: { title: "लिखने के बजाय बोलिए", heading: "बताइए क्या खराबी है", listen: "बोलने के लिए दबाएँ",
    listening: "सुन रहे हैं…", stop: "रोकें", sample: "नमूना वाक्य लें", clear: "मिटाएँ",
    heard: "फ़ोन ने क्या सुना", ticket: "फ़ोन ने क्या लिखा", accept: "यही भेजें",
    urgent: "तुरंत ध्यान चाहिए", standard: "सामान्य", noMic: "यह डिवाइस सुन नहीं सकता — लिखिए, या नमूना वाक्य लीजिए",
    onDevice: "इसी फ़ोन पर · कोई क्लाउड नहीं", trade: "काम का प्रकार इन शब्दों से पहचाना" },
  mr: { title: "लिहिण्याऐवजी बोला", heading: "काय बिघडले आहे ते सांगा", listen: "बोलण्यासाठी दाबा",
    listening: "ऐकत आहोत…", stop: "थांबा", sample: "नमुना वाक्य घ्या", clear: "पुसा",
    heard: "फोनने काय ऐकले", ticket: "फोनने काय लिहिले", accept: "हेच पाठवा",
    urgent: "तातडीचे", standard: "नेहमीचे", noMic: "हे उपकरण ऐकू शकत नाही — लिहा, किंवा नमुना वाक्य घ्या",
    onDevice: "याच फोनवर · क्लाउड नाही", trade: "कामाचा प्रकार या शब्दांवरून ओळखला" },
} as const;

export function VoiceIntake({ locale, service, onAccept }: Props) {
  const l = LABEL[locale] ?? LABEL.en;
  const caps = useMemo(() => readCapabilities(), []);
  const [heard, setHeard] = useState("");
  const [listening, setListening] = useState(false);
  const recognition = useRef<unknown>(null);

  const ticket = heard.trim() ? parseOnDevice(service, heard, locale) : null;

  useEffect(() => () => {
    const active = recognition.current as { stop?: () => void } | null;
    active?.stop?.();
  }, []);

  function listen() {
    if (!caps.speechToText) return;
    const w = window as unknown as Record<string, new () => unknown>;
    const Ctor = (w.SpeechRecognition || w.webkitSpeechRecognition) as new () => Record<string, unknown>;
    const r = new Ctor();
    recognition.current = r;
    r.lang = SPEECH_TAG[locale];
    r.interimResults = true;
    r.continuous = false;
    r.onresult = (event: { results: ArrayLike<ArrayLike<{ transcript: string }>> }) => {
      let text = "";
      for (let i = 0; i < event.results.length; i++) text += event.results[i][0].transcript;
      setHeard(text);
    };
    r.onend = () => setListening(false);
    r.onerror = () => setListening(false);
    setListening(true);
    (r as { start: () => void }).start();
  }

  function stop() {
    (recognition.current as { stop?: () => void } | null)?.stop?.();
    setListening(false);
  }

  return (
    <section className="odPanel" aria-label={l.heading}>
      <div className="odHead">
        <div>
          <p className="eyebrow">{l.title}</p>
          <h2>{l.heading}</h2>
        </div>
        <span className="odBadge">{l.onDevice}</span>
      </div>

      <div className="odControls">
        {caps.speechToText ? (
          <button
            type="button"
            className={listening ? "odMic live" : "odMic"}
            onClick={() => (listening ? stop() : listen())}
            aria-pressed={listening}
          >
            <span className="odMicGlyph" aria-hidden="true">●</span>
            <span>{listening ? l.stop : l.listen}</span>
          </button>
        ) : (
          <p className="odNoMic">{l.noMic}</p>
        )}
        <button type="button" className="secondary compact" onClick={() => setHeard(SAMPLE[locale])}>
          {l.sample}
        </button>
        {heard && (
          <button type="button" className="ghost compact" onClick={() => setHeard("")}>
            {l.clear}
          </button>
        )}
      </div>

      {listening && (
        <div className="odWave" aria-hidden="true">
          <i /><i /><i /><i /><i /><i /><i />
        </div>
      )}

      <label className="field odHeard">
        <span>{l.heard}</span>
        <textarea value={heard} onChange={(event) => setHeard(event.target.value)} rows={2} />
      </label>

      {ticket && (
        <div className="odTicket">
          <p className="eyebrow">{l.ticket}</p>
          <strong>{ticket.summary}</strong>
          <div className="odTags">
            <span className={ticket.urgency === "urgent" ? "odTag urgent" : "odTag"}>
              {ticket.urgency === "urgent" ? l.urgent : l.standard}
            </span>
            <span className="odTag">{ticket.suggestedService}</span>
          </div>
          {ticket.detectedFrom.length > 0 && (
            <small className="odDetected">
              {l.trade}: {ticket.detectedFrom.join(", ")}
            </small>
          )}
          <p className="odSafety">{ticket.safetyNote}</p>
          <ul className="odQuestions">
            {ticket.scopeQuestions.map((question) => (
              <li key={question}>{question}</li>
            ))}
          </ul>
          <p className="odNotice">{ticket.notice}</p>
          <button type="button" onClick={() => onAccept(ticket)}>{l.accept}</button>
        </div>
      )}
    </section>
  );
}

/**
 * Reads a decision out loud, so "why did I not get that job" has an answer for a member who
 * cannot read it. Uses the handset's own voice; nothing is sent anywhere.
 */
export function ReadAloud({ locale, text, label }: { locale: Locale; text: string; label?: string }) {
  const [speaking, setSpeaking] = useState(false);
  const caps = useMemo(() => readCapabilities(), []);

  useEffect(() => () => { if (typeof window !== "undefined") window.speechSynthesis?.cancel(); }, []);

  if (!caps.readAloud) return null;

  function speak() {
    window.speechSynthesis.cancel();
    if (speaking) { setSpeaking(false); return; }
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = SPEECH_TAG[locale];
    utterance.rate = 0.95;
    utterance.onend = () => setSpeaking(false);
    utterance.onerror = () => setSpeaking(false);
    setSpeaking(true);
    window.speechSynthesis.speak(utterance);
  }

  const fallbackLabel = { en: "Read this to me", hi: "यह मुझे पढ़कर सुनाइए", mr: "हे मला वाचून दाखवा" }[locale];
  return (
    <button type="button" className={speaking ? "odReadAloud live" : "odReadAloud"} onClick={speak}>
      <span aria-hidden="true">🔊</span>
      <span>{label ?? fallbackLabel}</span>
    </button>
  );
}
