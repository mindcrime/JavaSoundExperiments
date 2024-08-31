#!/bin/bash

ssh bozboxpi "rm -rf /home/prhodes/javasound2"
ssh bozboxpi "mkdir /home/prhodes/javasound2"
scp run.sh bozboxpi:/home/prhodes/javasound2/
scp target/javasound2-1.0.jar bozboxpi:/home/prhodes/javasound2/

echo "done"
