# Changelog

## 1.0.0 (2025-08-06)


### Features

* Adaptations towards MVH conformity ([2f2d0ef](https://github.com/dnpm-dip/rd-model/commit/2f2d0efbfaab39d6793222ad78852fc355c2a95a))
* Adapted RDCarePlan to refactored base CarePlan, with concomittant adaptations to completers and generators ([b9fa874](https://github.com/dnpm-dip/rd-model/commit/b9fa8741441a54b39bc9432d34056300ea803cc5))
* Added Alpha-ID-SE als CodeSystem.Publisher to domain model and as CodeSystemProvder implementation; minor corrections to JSON schema deriviation ([79871a3](https://github.com/dnpm-dip/rd-model/commit/79871a37d792089dd2af6db2a347ac64d7425824))
* Added new specified field SmallVariant.startPosition and .endPosition (with corresponding adaptation to generators); Adapted RDPatientRecord to refactored base PatientRecord ([0d5edb4](https://github.com/dnpm-dip/rd-model/commit/0d5edb4dfd77c8e923992fe8c284673c6f93dec3))
* Apparently final adaptations to model for MVH conformity ([ba7c6b0](https://github.com/dnpm-dip/rd-model/commit/ba7c6b038dc795bf3df9b4df7089b48a1d3198c3))
* Latest changes to model objects, with corresponding adaptations to completers and generators ([2ac8b5f](https://github.com/dnpm-dip/rd-model/commit/2ac8b5f89a980d770ca8ca5561862f27f0ca9076))
* Updated packaged HPO and ORDO; Upgraded to Scala 2.13.16 ([7832ff6](https://github.com/dnpm-dip/rd-model/commit/7832ff6dca20d0d6e85d5bf0cd6aaf7c7a2e1dfb))


### Bug Fixes

* Adaptation to use unified base Chromosome enum, instead of previous custom one extended by chrMT ([064858c](https://github.com/dnpm-dip/rd-model/commit/064858c2029d15de64d751ec3c247663361353eb))
* Adapted generation of Study references to yield valid values ([5e12223](https://github.com/dnpm-dip/rd-model/commit/5e122234bb5574f3a46ec8ffe5c6bd36c3168c52))
* Adapted generators to provide correct temporal relations among MDAT objects ([8ca5980](https://github.com/dnpm-dip/rd-model/commit/8ca598023647b76f60350bd7477d4f85ece45f5d))
* Adapted Patient generator to optional address ([c0a85f3](https://github.com/dnpm-dip/rd-model/commit/c0a85f32485a7563ac34ee914cb6528bd306b4c0))
* Adapted scalac linting and fixed many reported errors (mostly unused imports) ([ce305e7](https://github.com/dnpm-dip/rd-model/commit/ce305e74d23ae78c6524725f96996cf3b0b06bcc))
* Added forgotten .txt catalog files ([fd9e11a](https://github.com/dnpm-dip/rd-model/commit/fd9e11a9145e5122f0add381ffeea6b8895acc71))
* Added previously untracked OMIM catalog file ([f98c2d6](https://github.com/dnpm-dip/rd-model/commit/f98c2d622dff103cc11d8adece36c353a4f52228))
* Corrected cardinality of Variant.acmgClass and .zygosity to optional ([49528cb](https://github.com/dnpm-dip/rd-model/commit/49528cbb5c46815a88360b624dcc1de3993c032f))
* Corrected name of RDDiagnosis.Category name in JSON schema definition ([0287e9d](https://github.com/dnpm-dip/rd-model/commit/0287e9d415b724bcbbbd4814bc3e03eb8a95332b))
* Correction to NGSReport.Type subset used in generation ([9c1619f](https://github.com/dnpm-dip/rd-model/commit/9c1619f51ab7d4783e6665076b04760962bea1c1))
* Fixed linter errors (unused imports etc) in Test code ([3c960b3](https://github.com/dnpm-dip/rd-model/commit/3c960b3b3f1b0286f5956c3033074b51a3836479))
* Made RDDiagnosis.onsetDate optional to be in sync with Confluence specs ([7e71b65](https://github.com/dnpm-dip/rd-model/commit/7e71b6563536892f361e07a942147ff9df4203f3))
* Removed obsolete code from generator tests ([25a7d99](https://github.com/dnpm-dip/rd-model/commit/25a7d9937c0ae0d4d2737078cf35dbcd04118d43))
* Removed unnecessary code from tests ([ec952e6](https://github.com/dnpm-dip/rd-model/commit/ec952e64b564db51b62238f0f245389efcc9855a))
* typo in value of UpToFifteen ([a0330fb](https://github.com/dnpm-dip/rd-model/commit/a0330fb3432408155391f67a35344d58ea913f9e))
* typo in value of UpToFifteen ([68188b0](https://github.com/dnpm-dip/rd-model/commit/68188b0f749a428c7974191c5c83222ea7757643))
* Updated packaged HPO and ORDO ontology files ([8a4aec6](https://github.com/dnpm-dip/rd-model/commit/8a4aec645b173f612d23e1921f3470aab2ced95e))
* Upgraded scalatest and json-schema-validator versions; Code clean-up ([d0ebaef](https://github.com/dnpm-dip/rd-model/commit/d0ebaefb3d3b2a3474ce6a9dc4bc46336a90739d))
