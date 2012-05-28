#!/bin/bash
cd ~/NetbeansProjects/Desktop/
scp -r dist/*.* fanky@vb-deb.local:Shared/Carreras_beta/stable_v2.1a
scp -r dist/lib fanky@vb-deb.local:Shared/Carreras_beta/stable_v2.1a

