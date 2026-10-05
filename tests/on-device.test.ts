import assert from "node:assert/strict";
import test from "node:test";
import { parseOnDevice, SPEECH_TAG } from "../lib/on-device.ts";
import type { Locale } from "../lib/domain.ts";

const LOCALES: Locale[] = ["en", "hi", "mr"];
const DEVANAGARI = /[ऀ-ॿ]/;

test("a spark is treated as urgent, in every language", () => {
  assert.equal(parseOnDevice("electrician", "the switch sparks when I turn it on").urgency, "urgent");
  assert.equal(parseOnDevice("electrician", "स्विच चालू करताच ठिणगी पडते", "mr").urgency, "urgent");
  assert.equal(parseOnDevice("electrician", "स्विच से चिंगारी निकलती है और धुआं आता है", "hi").urgency, "urgent");
});

test("an ordinary request is not inflated into an emergency", () => {
  const calm = parseOnDevice("cleaning", "please send someone to clean the kitchen on Friday");
  assert.equal(calm.urgency, "standard");
  assert.ok(!/emergency/i.test(calm.safetyNote));
});

test("the member's own words choose the trade, and say which words did it", () => {
  const leak = parseOnDevice("cleaning", "water is leaking from the pipe under the tap");
  assert.equal(leak.suggestedService, "plumbing");
  assert.ok(leak.detectedFrom.length > 0, "it must show its working");
  leak.detectedFrom.forEach((word) => assert.ok(leak.summary.toLowerCase().includes(word)));
});

test("nothing is guessed when the words point nowhere", () => {
  const vague = parseOnDevice("carpentry", "please come tomorrow morning");
  assert.equal(vague.suggestedService, "carpentry", "the chosen service stands");
  assert.deepEqual(vague.detectedFrom, []);
});

test("the same words always produce the same ticket, so an intake can be replayed", () => {
  const once = parseOnDevice("electrician", "fan not working since morning", "hi");
  const twice = parseOnDevice("electrician", "fan not working since morning", "hi");
  assert.deepEqual(once, twice);
});

test("every ticket says it never left the phone", () => {
  for (const locale of LOCALES) {
    const ticket = parseOnDevice("plumbing", "tap is dripping", locale);
    assert.equal(ticket.mode, "on-device");
    assert.ok(ticket.notice.length > 0);
    if (locale !== "en") assert.match(ticket.notice, DEVANAGARI, `${locale} notice is untranslated`);
  }
});

test("an urgent ticket asks the extra safety question, a calm one does not", () => {
  assert.equal(parseOnDevice("electrician", "there is smoke from the meter").scopeQuestions.length, 3);
  assert.equal(parseOnDevice("electrician", "please replace a bulb").scopeQuestions.length, 2);
});

test("safety wording and questions reach the member in their own language", () => {
  for (const locale of ["hi", "mr"] as Locale[]) {
    const ticket = parseOnDevice("electrician", "ठिणगी", locale);
    assert.match(ticket.safetyNote, DEVANAGARI);
    ticket.scopeQuestions.forEach((q) => assert.match(q, DEVANAGARI));
  }
});

test("an empty description still produces a usable ticket rather than an empty one", () => {
  const blank = parseOnDevice("plumbing", "   ");
  assert.ok(blank.summary.trim().length > 0);
  assert.equal(blank.suggestedService, "plumbing");
});

test("the speech tags are Indian locales, so the member is heard correctly", () => {
  assert.deepEqual(SPEECH_TAG, { en: "en-IN", hi: "hi-IN", mr: "mr-IN" });
});

test("on-device intake cannot decide anything about a livelihood", () => {
  // The boundary is enforced by absence: there is no field here that could price, rank or dispatch.
  const ticket = parseOnDevice("electrician", "switch sparks") as unknown as Record<string, unknown>;
  for (const forbidden of ["workerId", "worker", "payout", "amount", "price", "rank", "score", "penalty", "decision"]) {
    assert.equal(ticket[forbidden], undefined, `on-device intake must not carry ${forbidden}`);
  }
});
