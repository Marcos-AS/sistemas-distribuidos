#!/bin/bash
#[STEP 0] - Check or create ssh key file

echo "Checking sshkey! "
sshkey_name=$HOME/.ssh/gcp
if [ -f "$sshkey_name" ]
then
    echo "SSH Key already exists"
else
   echo "Key not found"
   ssh-keygen -f $sshkey_name -t rsa -N '' -C "dmpetrocelli@gmail.com"
   ssh-add $sshkey_name
fi

filepath="C:\Users\leo_2\OneDrive\Documentos\GitHub\sistemas-distribuidos\TP 2\Ejercicio 2\ejercicio b\cloud\terraform\terraform.json"
bucket_name="bucket-imagenes-ej2b"
prefix="gke/state"

terraform init --reconfigure --var credentials_file_path=$filepath \
    --backend-config bucket=$bucket_name \
    --backend-config prefix=$prefix \
    --backend-config credentials=$filepath

terraform validate 

#terraform plan --var credentials_file_path=$filepath


#terraform apply --auto-approve  --var credentials_file_path=$filepath -lock=false

terraform destroy --auto-approve  --var credentials_file_path=$filepath -lock=false

