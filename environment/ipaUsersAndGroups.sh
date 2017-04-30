################################################################
# 
# 1) You must have an admin ticket before running this script
# 2) All the passwords are set to: changeme
# 3) You must change all the user passwords after running this script
#		The easiest way is to use kinit to request a ticket for the user
#		ie: kinit BigTopAdmin
#
################################################################


################################################################
# Create BigTop groups
################################################################
# BigTopAdminGroup
ipa group-add bigtopadmingroup --desc "Group granting users the privilege to write and update MarkLogic BigTop documents"

# BigTopUsers
ipa group-add bigtopusers --desc "Group granting users access to the MarkLogic BigTop REST server."

# BigTopReaderRedGroup
ipa group-add bigtopreaderredgroup --desc "Group granting users access to BigTop "red" documents."

# BigTopReaderBlueGroup
ipa group-add bigtopreaderbluegroup --desc "Group granting users access to BigTop "blue" documents."


################################################################
# Create BigTop users
################################################################
# BigTopAdmin
echo "changeme" | ipa user-add bigtopadmin --first=Admin --last=BigTop --password

# BigTopReaderRed
echo "changeme" | ipa user-add bigtopreaderred --first=Readerred --last=BigTop --password

# BigTopReaderBlue
echo "changeme" | ipa user-add bigtopreaderblue --first=Readerblue --last=BigTop --password

# BigTopReaderBoth
echo "changeme" | ipa user-add bigtopreaderboth --first=Readerboth --last=BigTop --password

# BigTopReaderNone
echo "changeme" | ipa user-add bigtopreadernone --first=Readernone --last=BigTop --password

# SomeOtherPerson
echo "changeme" | ipa user-add someotherperson --first=Person --last=Someother --password

################################################################
# Add users to the appropriate groups
################################################################
# Add admin user to admin group
ipa group-add-member bigtopadmingroup --users={bigtopadmin}

# Add everybody to the bigtopusers group
ipa group-add-member bigtopusers --users={bigtopadmin,bigtopreaderred,bigtopreaderblue,bigtopreaderboth,bigtopreadernone}

# Add admin and the red user to the bigtopreaderredgroup group
ipa group-add-member bigtopreaderredgroup --users={bigtopadmin,bigtopreaderred,bigtopreaderboth}

# Add admin and the blue user to the bigtopreaderbluegroup group
ipa group-add-member bigtopreaderbluegroup --users={bigtopadmin,bigtopreaderblue,bigtopreaderboth}
