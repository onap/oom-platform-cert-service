#!/bin/bash

configureEjbca() {
    ejbca.sh config cmp addalias --alias cmpRA
    ejbca.sh config cmp updatealias --alias cmpRA --key operationmode --value ra
    ejbca.sh ca editca --caname ManagementCA --field cmpRaAuthSecret --value mypassword
    ejbca.sh config cmp updatealias --alias cmpRA --key responseprotection --value signature
    ejbca.sh config cmp updatealias --alias cmpRA --key authenticationmodule --value 'HMAC;EndEntityCertificate'
    ejbca.sh config cmp updatealias --alias cmpRA --key allowautomatickeyupdate --value true
    ejbca.sh ca importprofiles -d /opt/primekey/custom_profiles
    #Profile name taken from certprofile filename (certprofile_<profile-name>-<id>.xml)
    ejbca.sh config cmp updatealias --alias cmpRA --key ra.certificateprofile --value CUSTOM_ENDUSER
    #ID taken from entityprofile filename (entityprofile_<profile-name>-<id>.xml)
    ejbca.sh config cmp updatealias --alias cmpRA --key ra.endentityprofileid --value 1356531849
    caSubject=$(ejbca.sh ca getcacert --caname ManagementCA -f /dev/stdout | grep 'Subject' | sed -e "s/^Subject: //" | sed -n '1p')
    ejbca.sh config cmp updatealias --alias cmpRA --key defaultca --value "$caSubject"
    ejbca.sh config cmp dumpalias --alias cmpRA
    ejbca.sh config cmp addalias --alias cmp
    ejbca.sh config cmp updatealias --alias cmp --key allowautomatickeyupdate --value true
    ejbca.sh config cmp updatealias --alias cmp --key responseprotection --value pbe
    ejbca.sh ra addendentity --username Node123 --dn "CN=Node123" --caname ManagementCA --password mypassword --type 1 --token USERGENERATED
    ejbca.sh ra setclearpwd --username Node123 --password mypassword
    ejbca.sh config cmp updatealias --alias cmp --key extractusernamecomponent --value CN
    ejbca.sh config cmp dumpalias --alias cmp
    ejbca.sh ca getcacert --caname ManagementCA -f /dev/stdout > cacert.pem
    caSubject=$(ejbca.sh ca getcacert --caname ManagementCA -f /dev/stdout | grep 'Subject' | sed -e "s/^Subject: //" | sed -n '1p')
    ejbca.sh config cmp updatealias --alias cmpRA --key defaultca --value "$caSubject"
    ejbca.sh config cmp dumpalias --alias cmpRA

    ejbca.sh roles addrole "Certificate Update Admin"
    ejbca.sh roles changerule "Certificate Update Admin" /ca/ManagementCA/ ACCEPT
    ejbca.sh roles changerule "Certificate Update Admin" /ca_functionality/create_certificate/ ACCEPT
    ejbca.sh roles changerule "Certificate Update Admin" /endentityprofilesrules/Custom_EndEntity/ ACCEPT
    ejbca.sh roles changerule "Certificate Update Admin" /ra_functionality/edit_end_entity/ ACCEPT
    ejbca.sh roles addrolemember "Certificate Update Admin" ManagementCA WITH_ORGANIZATION --value Linux-Foundation
}

configureEjbca
