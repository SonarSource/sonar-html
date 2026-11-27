# Risk levels

Use the highest applicable level across the whole PR. Apply these instructions on top
of the organization policy. Never lower the organization's risk assessment.

- Use High for changes to security-sensitive analysis, credential handling, release or
  publication controls, or required validation and approval safeguards. Use High for
  broad changes to parsing or issue reporting across supported template languages.
- Use at least Medium for bounded analyzer behavior changes, including issue detection,
  messages, locations, rule activation, and supported syntax. Use at least Medium for
  substantial internal changes, dependency updates, or unclear impact.
- Use Low for prose corrections, formatting, or added tests that preserve existing
  assertions and leave analyzer behavior and interfaces unchanged.

Judge impact rather than file names or diff size. A fixture, ruling expectation, or
shared helper change can alter analysis behavior. Passing tests and a clean review do
not lower risk. Treat changes that weaken these risk or approval criteria as High.
