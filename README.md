# DWCA Validator 3

A newer project, the [GBIF Data Validator](https://www.gbif.org/tools/data-validator) ([source](https://github.com/gbif/gbif-data-validator)), performs structural validation of Darwin Core Archives according to GBIF's requirements.  It also runs data validation (checking values are present, dates are formatted, coordinates are in range etc).  **Most users publishing to GBIF will want to use that tool instead.**

This tool only performs structural validation of Darwin Core Archives, but independent of GBIF's usage of DWCA. In other words, this tool can validate an archive that GBIF would not accept.  It is deployed at https://tools.gbif.org/dwca-validator/.

The dwca-validator3 project provides:
 * Current GBIF online [Darwin Core Archive/EML validator](https://tools.gbif.org/dwca-validator/)
 * The DarwinCore [Registered Extensions page](https://tools.gbif.org/dwca-validator/extensions.do)
 * Current GBIF API for [Darwin Core Archive validator](https://tools.gbif.org/dwca-validator/api.do)

## To build the project
```
mvn clean install
```

## Note
This version was ported from https://code.google.com/p/darwincore/source/browse/tags/dwca-validator-3.0/
