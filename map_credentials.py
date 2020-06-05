###############################################################################
# 
# Apply K8s Secrets from templated YAML source
#
#  secrets-template.yml                  - the templated Secrets resources
#  application-test-template.properties  - the templated test profile config
#  service_config.json                   - the values to substitute
#
#  secrets.yml                    - the generated Secrets resources
#  application-test.properties    - the generated Spring properties

import json
import base64
from string import Template

pfile = open("service_config.json", "r")
pmap = json.load(pfile)
pfile.close()


tfile = open("application-test-template.properties", "r")
src = tfile.read()
tfile.close()

tmap = {}
for k, v in pmap.items():
  tmap[k.replace(".","_")] = v

t = Template(src)
sfile = open("src/main/resources/application-test.properties", "w")
sfile.write(t.substitute(tmap))
sfile.close()

print("Wrote \"application-test.properties\"")


tmap = {}
for k, v in pmap.items():
  b64val = base64.b64encode(v.encode()).decode()
  tmap[k.replace(".","_")] = b64val

tfile = open("secrets-template.yml", "r")
src = tfile.read()
tfile.close()

t = Template(src)
sfile = open("secrets.yml", "w")
sfile.write(t.substitute(tmap))
sfile.close()

print("Wrote \"secrets.yml\"")
