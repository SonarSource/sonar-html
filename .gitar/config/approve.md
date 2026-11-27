# Approval criteria

Approve only when the current review is complete, there are zero unresolved findings,
and Gitar's current whole-PR risk assessment is **Low** under the combined organization
and repository risk policy.

Withhold approval for Medium or High risk. Withhold approval when risk is missing,
unavailable, unrecognized, or stale for the current PR changes. A clean review, a small
diff, or a PR label alone does not establish Low risk.

Low risk does not bypass other approval criteria or code-host merge requirements.
