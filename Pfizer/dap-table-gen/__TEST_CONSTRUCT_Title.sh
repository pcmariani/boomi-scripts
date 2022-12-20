#!/usr/bin/env bash


[[ -e "DAP.TBL.STR.01_out.properties" ]] && rm DAP.TBL.STR.01_out.properties
[[ -e "DAP.TBL.STR.02_out.properties" ]] && rm DAP.TBL.STR.02_out.properties

dir_table="$@"
# dir="$1"
# table="$2"
# sed -i 's/\(@file("\).*\(_.*Config\).*.json/\1'"$dir"'\/\2.json/' "$dir"/main.properties

echo "$dir_table".properties
boomi -d _start.dat -p "$dir_table".properties DAP.TBL.STR.01__PARSE_Params.b.groovy
# grep -v '.*Config.*' DAP.TBL.STR.01_out.properties | sed 's/document\.dynamic\.userdefined\.//'
echo 

boomi -d _start.dat -p DAP.TBL.STR.01_out.properties DAP.TBL.STR.021__CONSTRUCT_Title.b.groovy
# grep -v '.*Config.*' DAP.TBL.STR.01_out.properties | sed 's/document\.dynamic\.userdefined\.//'

# boomi -d "$dir_table".dat -p DAP.TBL.STR.01_out.properties DAP.TBL.STR.02__CREATE_RowSets.b.groovy
# grep -v '.*Config.*' DAP.TBL.STR.02_out.properties | sed 's/document\.dynamic\.userdefined\.//'

# boomi -d "$dir_table".dat -p DAP.TBL.STR.01_out.properties DAP.TBL.STR.025__REMOVE_Empty_Rows_2.b.groovy 
# grep -v '.*Config.*' DAP.TBL.STR.025_out.properties | sed 's/document\.dynamic\.userdefined\.//'

# echo "$dir_table"
# boomi -d "$dir_table".dat -p DAP.TBL.STR.01_out.properties DAP.TBL.STR.03__BUILD_Html.b.groovy
# grep -v '.*Config.*' DAP.TBL.STR.03_out.properties | sed 's/document\.dynamic\.userdefined\.//'


# boomi -d ./DAP.TBL.STR.03_out.dat -p DAP.TBL.STR.03_out.properties DAP.TBL.STR.04__REMOVE_Empty_Rows.b.groovy
# grep -v '.*Config.*' DAP.TBL.STR.04_out.properties | sed 's/document\.dynamic\.userdefined\.//'

# [[ -e "DAP.TBL.STR.02_out.properties" ]] && \
# cat DAP.TBL.STR.02_out.properties | sed 's/document\.dynamic\.userdefined\.//'

# mv DAP.TBL.STR.03_out.dat DAP.TBL.STR.03_out.html
# mv DAP.TBL.STR.04_out.dat DAP.TBL.STR.04_out.html
