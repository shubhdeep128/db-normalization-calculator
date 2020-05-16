package com.company;

import java.awt.desktop.SystemSleepEvent;
import java.lang.reflect.Array;
import java.util.*;

public class Relation {
    String[] attributes = new String[100];
    String[] dependencyList = new String[200];
    int countDependency = 0;
    public ArrayList<ArrayList<String>> depValsList = new ArrayList<>();
    public ArrayList<ArrayList<String>> depKeyList = new ArrayList<>();
    public ArrayList<HashSet<String>> relKeyList = new ArrayList<>();
    public HashSet<String> candidateSet = new HashSet<>();
    public ArrayList<ArrayList<String>> getDepKeyList() {
        return depKeyList;
    }

    public ArrayList<ArrayList<String>> getDepValsList() {
        return depValsList;
    }

    public int getCountDependency() {
        return countDependency;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public String[] getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(String[] dependencyList) {
        this.countDependency = dependencyList.length;
        for (int i = 0; i < dependencyList.length; i++) {
            String[] depArr = dependencyList[i].split(">",0);
            String[] tempKeyList = depArr[0].split(",",0);
            String[] tempValueList = depArr[1].split(",",0);
            ArrayList<String> keyList = new ArrayList<>();
            for (String s: tempKeyList
            ) {
                keyList.add(s);
            }
            ArrayList<String> valueList = new ArrayList<>();
            for (String s: tempValueList
            ) {
                valueList.add(s);
            }
            this.depKeyList.add(keyList);
            this.depValsList.add(valueList);
        }
        this.dependencyList = dependencyList;
    }

    public String[] parseAttributes(String str){
        String[] attr = str.split(" ",0);
        return attr;
    }

    public String[] parseDependency(String str){
        String[] dependencyList = str.split(";",0);
        return dependencyList;
    }

    public HashSet<String> findClosure(HashSet<String>querySet,ArrayList<ArrayList<String>> keyList,ArrayList<ArrayList<String>> valsList){

        HashSet<String> closureSet = (HashSet<String>) querySet.clone();

        HashSet<String> oldSet;
        do {
            oldSet = (HashSet<String>) closureSet.clone();
            for (int i = 0; i < keyList.size(); i++) {

                HashSet<String> tempKey = new HashSet<String>();
                for (String s:keyList.get(i)
                ) {
                    tempKey.add(s);
                }
                HashSet<String> tempValue = new HashSet<String>();
                for (String s:valsList.get(i)
                ) {
                    tempValue.add(s);
                }
                if(closureSet.containsAll(tempKey)){
                    closureSet.addAll(tempValue);
                }
            }

        }while(!closureSet.equals(oldSet));

        return  closureSet;
    }

    public ArrayList<ArrayList<String>> generatePerm(String[] attributes){
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        permuteHelper(list,new ArrayList<>(),attributes);
        return list;
    }

    void permuteHelper(ArrayList<ArrayList<String>> list,ArrayList<String> resultList, String[] attr){
        if(resultList.size() == attr.length){
            list.add(new ArrayList<>(resultList));
        }
        else {
            for (int i = 0; i < attr.length; i++) {
                if(resultList.contains(attr[i])){
                    continue;
                }

                resultList.add(attr[i]);

                permuteHelper(list,resultList,attr);

                resultList.remove(resultList.size() - 1);

            }
        }

    }

    public void setRelKeyList(String[] attributes) {
        ArrayList<ArrayList<String>> attrList = this.generatePerm(attributes);

        for (ArrayList<String> attr: attrList
             ) {
            HashSet<String> key = getKey(attr);
            if(this.relKeyList.contains(key)){ continue; }
            relKeyList.add(key);
        }
        setCandidateSet(relKeyList);
    }

    public ArrayList<HashSet<String>> getRelKeyList() {
        return relKeyList;
    }

    public void setCandidateSet(ArrayList<HashSet<String>> relKeyList) {
        for (HashSet<String> set: relKeyList
             ) {
            for (String attr: set
                 ) {
                this.candidateSet.add(attr);
            }
        }
    }

    public HashSet<String> getKey(ArrayList<String> attributes) {
        HashSet<String> attrSet = new HashSet<>();
        for (String s: attributes
             ) {
            attrSet.add(s);
        }

        ArrayList<String> k = attributes;
        ListIterator iter = k.listIterator();

        while (iter.hasNext()){
            HashSet<String> tempSet = new HashSet<>();
            for (String s: k
                 ) {
                tempSet.add(s);
            }

            String str = (String) iter.next();
            tempSet.remove(str);
            if(findClosure(tempSet,this.depKeyList,this.depValsList).containsAll(attrSet)){
                iter.remove();
            }

        }

        HashSet<String> resultSet = new HashSet<>();
        for (String s:k
             ) {
            resultSet.add(s);
        }
        return resultSet;
    }

    public void decomposer(){
        char highestNf = '1';
        if(this.checkTwoNF()){
            highestNf = '2';
            System.out.println("2NF = true");
        }
        else{
            System.out.println("2NF = false");
        }
        if(this.checkThreeNF()){
            highestNf = '3';
            System.out.println("3NF = true");
        }
        else{
            System.out.println("3NF = false");
        }
        if(this.checkBCNF()){
            highestNf = '4';
            System.out.println("BCNF = true");
        }
        else{
            System.out.println("BCNF = false");
        }
        System.out.println();
        switch (highestNf){
            case '1':
                System.out.println("Decomposition to 2NF\n");
                this.OneToTwo();
                break;
            case '2':
                System.out.println("Decomposition to 3NF\n");
                this.TwoToThree();
                break;
            case '3':
                System.out.println("Decomposition to BCNF\n");
                this.ThreeToBCNF();
                break;
            default:
                break;
        }


    }

    public boolean checkTwoNF(){
        boolean isTwo = true;
        for (int i = 0; i < depKeyList.size(); i++) {
            HashSet<String> setVal = new HashSet<>(depValsList.get(i));
            HashSet<String> setKey = new HashSet<>(depKeyList.get(i));

            setVal.removeAll(this.candidateSet);
            if(setVal.isEmpty()){
                continue;
            }

            for (HashSet<String> candKey: this.relKeyList
                 ) {
                if(candKey.containsAll(setKey) && !candKey.equals(setKey) ){
                    return false;
                }
            }
        }
        return isTwo;
    }

    public boolean checkThreeNF(){
        if(!this.checkTwoNF()) return false;

        boolean isThree= false;

        for (int i = 0;i<this.depKeyList.size();i++){
            HashSet<String> setKey = new HashSet<>();
            HashSet<String> setVal = new HashSet<>();
            for (String s:depKeyList.get(i)
            ) {
                setKey.add(s);
            }
            for (String s: depValsList.get(i)
            ) {
                setVal.add(s);
            }
            isThree = false;
            for (HashSet<String> candKey: this.relKeyList
                 ) {
                if(setKey.containsAll(candKey)){
                    isThree = true;
                    break;
                }
            }
            if(isThree) continue;

            if(this.candidateSet.containsAll(setVal)){
                isThree = true;
                continue;
            }
            else {
                return false;
            }
        }

        return isThree;
    }

    public boolean checkBCNF(){
        if(!this.checkThreeNF()) return false;
        boolean isBCNF = true;
        for (int i = 0; i < this.depKeyList.size();  i++) {
            HashSet<String> setKey = new HashSet<>();
            for (String s:depKeyList.get(i)
            ) {
                setKey.add(s);
            }
            isBCNF = false;
            for (HashSet<String> candKey: this.relKeyList
                 ) {
                if(setKey.containsAll(candKey)){
                    isBCNF = true;
                    break;
                }

            }
            if(!isBCNF){
                isBCNF = false;
                return isBCNF;
            }
        }
        return isBCNF;
    }

    public boolean funcDepEquals(ArrayList<ArrayList<String>> fkeyList,ArrayList<ArrayList<String>> fvalList,ArrayList<ArrayList<String>> ekeyList,ArrayList<ArrayList<String>> evalList){
        boolean flag = false;

        for (int i = 0; i < ekeyList.size(); i++) {
            HashSet<String> eKeySet = new HashSet<>(ekeyList.get(i));
            HashSet<String> eValSet = new HashSet<>(evalList.get(i));
            if(!findClosure(eKeySet,fkeyList,fvalList).containsAll(eValSet)){
                return false;
            }
        }

        for (int i = 0; i < fkeyList.size(); i++) {
            HashSet<String> fKeySet = new HashSet<>(fkeyList.get(i));
            HashSet<String> fValSet = new HashSet<>(fvalList.get(i));
            if(!findClosure(fKeySet,ekeyList,evalList).containsAll(fValSet)){
                return false;
            }
        }

        return true;
    }

    public ArrayList<ArrayList<ArrayList<String>>> findMinimalCover(ArrayList<ArrayList<String>> keyList,ArrayList<ArrayList<String>>valsList){
        ArrayList<ArrayList<String>> fkeyList = (ArrayList<ArrayList<String>>) keyList.clone();
        ArrayList<ArrayList<String>> fvalList = (ArrayList<ArrayList<String>>) valsList.clone();
        for (int i = 0; i < fkeyList.size(); i++) {
            ArrayList<String> setKey = fkeyList.get(i);
            ArrayList<String> setVal = fvalList.get(i);
            if (setVal.size() <= 1) {
                continue;
            }


            fkeyList.remove(i);
            fvalList.remove(i);

            for (String s : setVal
            ) {
                fkeyList.add(setKey);
                ArrayList<String> tempVal = new ArrayList<>();
                tempVal.add(s);
                fvalList.add(tempVal);
            }
        }



        for (int i = 0; i < fkeyList.size(); i++) {
            if(fkeyList.get(i).size() <= 1){
                continue;
            }
            for (int j = 0; j< fkeyList.get(i).size(); j++) {

                String str = fkeyList.get(i).get(j);
                ArrayList<ArrayList<String>> tempKeyList = new ArrayList<>();
                ArrayList<ArrayList<String>> tempValList = new ArrayList<>();

                for (ArrayList<String> key: fkeyList
                ) {
                    ArrayList<String> temp = new ArrayList<>();
                    for (String s: key
                    ) {
                        temp.add(s);
                    }
                    tempKeyList.add(temp);
                }

                for (ArrayList<String> val: fvalList
                ) {
                    ArrayList<String> temp = new ArrayList<>();
                    for (String s: val
                    ) {
                        temp.add(s);
                    }
                    tempValList.add(temp);
                }

                ArrayList<String>tempKey = fkeyList.get(i); ArrayList<String>tempVal = fvalList.get(i);

                tempKeyList.remove(tempKey); tempValList.remove(tempVal);

                tempKey.remove(str);

                tempKeyList.add(tempKey); tempValList.add(tempVal);

                HashSet<String> tempSet = new HashSet<>();
                for (String s: tempKey
                ) {
                    tempSet.add(s);
                }

                if(findClosure(tempSet,tempKeyList,tempValList).contains(str)){
                    fkeyList.remove(fkeyList.get(i)); fvalList.remove(fvalList.get(i));
                    fkeyList.add(tempKey); fvalList.add(tempVal);
                }


            }
        }
        for (int i = 0; i < fkeyList.size(); i++) {
            for (int j = 0; j < fkeyList.size(); j++) {
                if(i == j){
                    continue;
                }
                if(fkeyList.get(i).equals(fkeyList.get(j)) && fvalList.get(i).equals(fvalList.get(j))){
                    fkeyList.remove(j); fvalList.remove(j);
                }
            }
        }


        for (int i = 0; i < fkeyList.size(); i++) {
            ArrayList<ArrayList<String>> tempKeyList = new ArrayList<>(fkeyList);
            ArrayList<ArrayList<String>> tempValList = new ArrayList<>(fvalList);

            tempKeyList.remove(i);
            tempValList.remove(i);

            if(funcDepEquals(tempKeyList,tempValList,fkeyList,fvalList)){
                fvalList.remove(i);
                fkeyList.remove(i);
            }


        }


        //System.out.println("Done3 " + fkeyList +" "+fvalList);
        ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<>();
        result.add(fkeyList);
        result.add(fvalList);
        return result;
    }



    public void OneToTwo(){
        ArrayList<Relation> decomposedRelation = new ArrayList<>();
        ArrayList<Relation> tempRelations = new ArrayList<>();

        tempRelations.add(this);

        while (tempRelations.size()>0){
            Relation g = tempRelations.get(0);

            if(!g.checkTwoNF()) {
                tempRelations.remove(g);
                HashSet<String> gAttrSet = new HashSet<>();
                for (String s : g.attributes
                ) {
                    gAttrSet.add(s);
                }

                for (int i = 0; i < g.depKeyList.size(); i++) {
                    HashSet<String> gKeySet = new HashSet<>(g.depKeyList.get(i));
                    HashSet<String> gValSet = new HashSet<>(g.depValsList.get(i));
                    boolean flag = true;
                    for (HashSet<String> candKey : g.relKeyList
                    ) {
                        if (candKey.containsAll(gKeySet) && !gKeySet.containsAll(candKey)) {
                                flag = false;
                                break;
                        }
                    }
                    if(flag) continue;

                    Relation r = new Relation();
                    Relation t = new Relation();
                    HashSet<String> attrSet = findClosure(gKeySet,g.depKeyList,g.depValsList);
                    String[] attrArr = new String[attrSet.size()];
                    int ctr = 0;
                    for (String s: attrSet
                    ) {
                        attrArr[ctr++] = s;
                    }
                    r.setAttributes(attrArr);
                    r.relKeyList.add(gKeySet);
                    HashSet<String> bSet = new HashSet<>(attrSet);
                    bSet.removeAll(g.candidateSet);
                    gAttrSet.removeAll(bSet);
//                    System.out.println("LIST TEST " + g.depKeyList + " " + g.depValsList);
                    ArrayList<ArrayList<String>> toRemoveKeys = new ArrayList<>();
                    ArrayList<ArrayList<String>> toRemoveVals = new ArrayList<>();
                    for (int j = 0; j < g.depKeyList.size(); j++) {
//                        System.out.println("Testing " + g.depKeyList.get(j) + " " + g.depValsList.get(j));
                        HashSet<String> tempSet = new HashSet<>(g.depKeyList.get(j));
                        if(attrSet.containsAll(tempSet)){
//                            System.out.println(true);
                            r.depKeyList.add(g.depKeyList.get(j));
                            r.depValsList.add(g.depValsList.get(j));
                            toRemoveKeys.add(g.depKeyList.get(j));
                            toRemoveVals.add(g.depValsList.get(j));
                        }
                    }

                    for (int j = 0; j < toRemoveKeys.size(); j++) {
                        g.depKeyList.indexOf(toRemoveKeys.get(j));
                        g.depValsList.remove(g.depKeyList.indexOf(toRemoveKeys.get(j)));
                        g.depKeyList.remove(toRemoveKeys.get(j));
                    }

                    String[] tArr = new String[gAttrSet.size()];
                    int c = 0;
                    for (String s: gAttrSet
                         ) {
                        tArr[c++] = s;
                    }
                    t.setAttributes(tArr);
                    t.depKeyList = g.depKeyList;
                    t.depValsList = g.depValsList;
                    t.setRelKeyList(tArr);

                    tempRelations.add(t);
                    tempRelations.add(r);

                }
            }
            else
            {
                decomposedRelation.add(g);
                tempRelations.remove(g);
            }

        }
        ArrayList<Relation> dList = decomposedRelation;
        for (int i = 0; i < dList.size(); i++) {
            Relation r = dList.get(i);
            System.out.println("Relation " + (i+1));
            HashSet<String> attrSet = new HashSet<>();
            for (String s: r.getAttributes()
            ) {
                attrSet.add(s);
            }
            System.out.println("Attributes: " + attrSet );


            System.out.println("Functional Dependencies: ");
            for (int j = 0; j < r.depValsList.size(); j++) {
                System.out.println(r.depKeyList.get(j) + "-->" + r.depValsList.get(j));
            }

//            r.setRelKeyList(r.getAttributes());
            System.out.println("Candidate Keys are");
            ArrayList<HashSet<String>> relKeyList = r.getRelKeyList();

            for (HashSet<String> key: relKeyList
            ) {
                System.out.println(key);
            }
            System.out.println();
        }
    }

    public void TwoToThree(){
        for (HashSet<String>candKey: this.relKeyList
             ) {
            TwoToThreeHelper(candKey);
        }
    }

    public ArrayList<Relation> TwoToThreeHelper(HashSet<String> candKey){
        ArrayList<Relation> decompList= new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> minCover = this.findMinimalCover(this.depKeyList,this.depValsList);
        ArrayList<ArrayList<String>> fkeyList = minCover.get(0);
        ArrayList<ArrayList<String>> fvalList = minCover.get(1);
        Relation candDecomp = new Relation();
        for (int i = 0; i < fkeyList.size(); i++) {
            HashSet<String> keySet = new HashSet<>(fkeyList.get(i));
            if(keySet.containsAll(candKey) && candKey.containsAll(keySet)){
                candDecomp.depKeyList.add(fkeyList.get(i));
                candDecomp.depValsList.add(fvalList.get(i));
                fkeyList.remove(i);
                fvalList.remove(i);
            }
        }
        HashSet<String> attrSet = new HashSet<>();
        for (int i = 0; i < candDecomp.depKeyList.size(); i++) {
            for (ArrayList<String> key: candDecomp.depKeyList
                 ) {
                for (String s: key
                     ) {
                    attrSet.add(s);
                }
            }
            for (ArrayList<String> val: candDecomp.depValsList
            ) {
                for (String s: val
                ) {
                    attrSet.add(s);
                }
            }
        }
        int c = 0;
        String[] attr = new String[attrSet.size()];
        for (String s: attrSet
        ) {
            attr[c++] = s;
        }
        candDecomp.setAttributes(attr);
        int relNo = 1;
        System.out.println("Relation " + relNo++);
        System.out.println("Attributes: " +  attrSet );


        System.out.println("Functional Dependencies: ");
        for (int i = 0; i < candDecomp.depValsList.size(); i++) {
            System.out.println(candDecomp.depKeyList.get(i) + "-->" + candDecomp.depValsList.get(i));
        }

        candDecomp.setRelKeyList(candDecomp.getAttributes());
        System.out.println("Candidate Keys are");
        ArrayList<HashSet<String>> relKeyList = candDecomp.getRelKeyList();

        for (HashSet<String> key: relKeyList
        ) {
            System.out.println(key);
        }

        for (int i = 0; i < fkeyList.size(); i++) {
            Relation rel = new Relation();
            rel.depKeyList.add(fkeyList.get(i));
            rel.depValsList.add(fvalList.get(i));
//            System.out.println(fkeyList.get(i) +" "+ fvalList.get(i));
            HashSet<String> relAttrSet = new HashSet<>();

            for (ArrayList<String> key: rel.depKeyList
            ) {
                for (String s: key
                ) {
                    relAttrSet.add(s);
                }
            }
            for (ArrayList<String> val: rel.depValsList
            ) {
                for (String s: val
                ) {
                    relAttrSet.add(s);
                }
            }
            int ctr = 0;
            String[] attrArr = new String[relAttrSet.size()];
            for (String s: relAttrSet
            ) {
                attrArr[ctr++] = s;
            }
            rel.setAttributes(attrArr);
            System.out.println("Relation " + relNo++);
            System.out.println("Attributes: " +  relAttrSet );

            System.out.println("Functional Dependencies: ");
            for (int j = 0; j < rel.depValsList.size(); j++) {
                System.out.println(rel.depKeyList.get(j) + "-->" + rel.depValsList.get(j));
            }

            rel.setRelKeyList(rel.getAttributes());
            System.out.println("Candidate Keys are");
            ArrayList<HashSet<String>> relKeyListIn = rel.getRelKeyList();
            for (HashSet<String> key: relKeyListIn
            ) {
                System.out.println(key);
            }
        }

        return decompList;

    }


    public void ThreeToBCNF(){

        ArrayList<Relation> dList = new ArrayList<>();
        boolean flag = true;
        dList.add(this);

        while (flag){
            flag = false;
            Relation r = new Relation();
            for (int i = 0; i < dList.size(); i++) {
                if(!dList.get(i).checkBCNF()){
                    flag = true;
                    r = dList.get(i);
                    dList.remove(i);
                    break;
                }
            }

            if(!flag) break;

            for (int i = 0; i < r.depKeyList.size(); i++) {
                HashSet<String> setkey= new HashSet<>(r.depKeyList.get(i));
                HashSet<String> setVal = new HashSet<>(r.depValsList.get(i));
                boolean superFlag = false;
                for (HashSet<String> candKey: r.relKeyList
                     ) {
                    if(setkey.containsAll(candKey)){
                        superFlag = true;
                        break;
                    }
                }

                if(superFlag) continue;

                Relation rel1 = new Relation();
                rel1.depKeyList.add(r.depKeyList.get(i));
                rel1.depValsList.add(r.depValsList.get(i));
                r.depKeyList.remove(i);
                r.depValsList.remove(i);
                rel1.candidateSet.addAll(setkey);
                HashSet<String> attrSet = new HashSet<>();
                attrSet.addAll(setkey);
                attrSet.addAll(setVal);
                String[] attrArr = new String[attrSet.size()];
                int ctr = 0;
                for (String s:attrSet
                     ) {
                    attrArr[ctr++] = s;
                }
                rel1.setAttributes(attrArr);
                rel1.setRelKeyList(attrArr);


                Relation rel2 = new Relation();
//                rel2.depKeyList = r.depKeyList;
//                rel2.depValsList = r.depValsList;

                HashSet<String> attrSet2 = new HashSet<>();

                for (int j = 0; j < r.depKeyList.size(); j++) {
                    ArrayList<String> tempKey = new ArrayList<>(r.depKeyList.get(j));
                    ArrayList<String> tempVal = new ArrayList<>(r.depValsList.get(j));

                    HashSet<String> tempClosure = findClosure(setVal,r.depKeyList,r.depValsList);
                    setVal.addAll(tempClosure);
                    setVal.removeAll(setkey);

//                    System.out.println("To remove: " + setVal);


                    tempKey.removeAll(setVal);
                    tempVal.removeAll(setVal);

                    rel2.depKeyList.add(tempKey);
                    rel2.depValsList.add(tempVal);
                    attrSet2.addAll(tempKey); attrSet2.addAll(tempVal);
                }
                String[] attrArr2 = new String[attrSet2.size()];
                ctr = 0;
                for (String s:attrSet2
                ) {
                    attrArr2[ctr++] = s;
                }

                rel2.setAttributes(attrArr2);
                rel2.setRelKeyList(attrArr2);

                dList.add(rel1);
                dList.add(rel2);

            }

        }
        for (int i = 0; i < dList.size(); i++) {
            Relation r = dList.get(i);
            System.out.println("Relation " + (i+1));
            HashSet<String> attrSet = new HashSet<>();
            for (String s: r.getAttributes()
            ) {
                attrSet.add(s);
            }
            System.out.println("Attributes: " + attrSet );


            System.out.println("Functional Dependencies: ");
            for (int j = 0; j < r.depValsList.size(); j++) {
                System.out.println(r.depKeyList.get(j) + "-->" + r.depValsList.get(j));
            }

            r.setRelKeyList(r.getAttributes());
            System.out.println("Candidate Keys are");
            ArrayList<HashSet<String>> relKeyList = r.getRelKeyList();

            for (HashSet<String> key: relKeyList
            ) {
                System.out.println(key);
            }
            System.out.println();
        }


    }

    public ArrayList<ArrayList<ArrayList<String>>> projectFD(Relation oldRel,Relation r, ArrayList<ArrayList<String>> oldKeyLIst, ArrayList<ArrayList<String>> oldValsLIst){
        ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<>();
        ArrayList<ArrayList<String>> resultKeys = new ArrayList<>();
        ArrayList<ArrayList<String>> resultVals = new ArrayList<>();

        ArrayList<String> newAttrSet = new ArrayList<>();
        for (String s:r.getAttributes()
             ) {
            newAttrSet.add(s);
        }

        for (int i = 0; i < (1<<newAttrSet.size()) - 1; i++) {
            ArrayList<String> subAttrSet = new ArrayList<>();
            for (int j = 0; j < newAttrSet.size(); j++) {
                if((i&(1<<j))!=0){
                    subAttrSet.add(newAttrSet.get(j));
                }
            }
            HashSet<String> tempSet = new HashSet<>(subAttrSet);
            ArrayList<String> rightSet = new ArrayList<>(findClosure(tempSet,oldKeyLIst,oldValsLIst));
            if(rightSet.size() == 0) continue;
            for (String s: rightSet
                 ) {
                ArrayList<String> right = new ArrayList<>();
                right.add(s);
                ArrayList<String> rAttrSet= new ArrayList<>();
                for (String str: r.getAttributes()
                     ) {
                    rAttrSet.add(str);
                }
                if(!subAttrSet.containsAll(right) && rAttrSet.containsAll(right)){
                    ArrayList<String> tempKey = new ArrayList<>(subAttrSet);
                    ArrayList<String> tempVal = new ArrayList<>(right);
                    resultKeys.add(tempKey); resultVals.add(tempVal);
                }

            }
        }

        result = findMinimalCover(resultKeys,resultVals);

        return  result;
    }

    public ArrayList<ArrayList<ArrayList<String>>> projectFDalt(Relation oldRel,Relation r, ArrayList<ArrayList<String>> oldKeyList, ArrayList<ArrayList<String>> oldValsList){
        ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<>();

        ArrayList<ArrayList<String>> resultKeys = new ArrayList<>();
        ArrayList<ArrayList<String>> resultVals = new ArrayList<>();

        ArrayList<String> attrList = new ArrayList<>();
        for (String s:oldRel.getAttributes()
             ) {
            attrList.add(s);
        }

        ArrayList<ArrayList<String>> powerSet = new ArrayList<>();



        return result;
    }

    public void ThreeToBCNFalt(){

        ArrayList<Relation> tempRelations = new ArrayList<>();
        ArrayList<Relation> decomposedRelations = new ArrayList<>();

        tempRelations.add(this);
        for (Relation t: tempRelations
             ) {
            System.out.println("Stuck" + t.depKeyList + " " + t.depValsList);
        }
        while (tempRelations.size()!=0){

            Relation t = tempRelations.get(0);
            System.out.println("Stuck" + t.depKeyList + " " + t.depValsList);
            if(!t.checkBCNF()){
                ArrayList<String> tempKey = new ArrayList<>();
                ArrayList<String> tempVal = new ArrayList<>();

                for (int i = 0; i < t.depKeyList.size(); i++) {

                    boolean createFlag = true;
                    ArrayList<String> setKey = new ArrayList<>(t.depKeyList.get(i));
                    ArrayList<String> setVal = new ArrayList<>(t.depValsList.get(i));
                    Collections.sort(setKey);

                    HashSet<String> attrset = new HashSet<>();
                    for (String s:t.getAttributes()
                         ) {
                        attrset.add(s);
                    }

                    for (HashSet<String> candkey: t.relKeyList
                         ) {
                        if(setKey.containsAll(candkey) && attrset.containsAll(setVal)){
                            createFlag = false;
                            break;
                        }
                    }

                    if(createFlag){
                        tempKey = setKey; tempVal = setVal;

                        Relation t1 = new Relation();
                        Relation t2 = new Relation();

                        HashSet<String> newAttrSet = new HashSet<>();
                        for (String s:tempKey
                             ) {
                            newAttrSet.add(s);
                        }
                        for (String s: tempVal
                             ) {
                            newAttrSet.add(s);
                        }

                        HashSet<String> oldAttrSet = new HashSet<>();
                        for (String s: t.getAttributes()
                             ) {
                            oldAttrSet.add(s);
                        }

                        oldAttrSet.removeAll(tempVal);

                        String[] newArr = new String[newAttrSet.size()];
                        int ctr = 0;
                        for (String s: newAttrSet
                             ) {
                            newArr[ctr++] = s;
                        }
                        t1.setAttributes(newArr);

                        String[] oldArr = new String[oldAttrSet.size()];
                        ctr = 0;
                        for (String s: oldAttrSet
                        ) {
                            oldArr[ctr++] = s;
                        }
                        t2.setAttributes(oldArr);
                        System.out.println("ello1");
                        ArrayList<ArrayList<ArrayList<String>>> resultfd = projectFD(t,t1,t.depKeyList,t.depValsList);
                        t1.depKeyList = resultfd.get(0);
                        t1.depValsList = resultfd.get(1);
                        t1.setRelKeyList(newArr);

                        System.out.println("ello2 " + t1.depKeyList + t2.depValsList);
                        resultfd = projectFD(t,t2,t.depKeyList,t.depValsList);
                        t2.depKeyList = resultfd.get(0);
                        t2.depValsList = resultfd.get(1);
                        t2.setRelKeyList(oldArr);

                        System.out.println("ello3 " + t2.depKeyList + t2.depValsList);

                        tempRelations.add(t1);
                        tempRelations.add(t2);
                        tempRelations.remove(t);
                        for (Relation te: tempRelations
                        ) {
                            System.out.println("StuckHere" + te.depKeyList + " " + te.depValsList);
                        }
                        break;

                    }

                }

            }
            else {
                decomposedRelations.add(t);
                tempRelations.remove(t);
                System.out.println("Removed " + t.depKeyList + " " + t.depValsList);

            }
        }
        System.out.println("Exited");
        ArrayList<Relation> ckRelationList = new ArrayList<>();
        for (HashSet<String> candKey: this.relKeyList
             ) {
            Relation tempRel = new Relation();
            String[] attrArr = new String[candKey.size()];
            int ctr = 0;
            for (String s: candKey
                 ) {
                attrArr[ctr++] = s;
            }
            tempRel.setAttributes(attrArr);
            ArrayList<ArrayList<ArrayList<String>>> resultfd = projectFD(this,tempRel,this.depKeyList,this.depValsList);
            System.out.println("hello4");
            tempRel.depKeyList = resultfd.get(0);
            tempRel.depValsList = resultfd.get(1);
            tempRel.setRelKeyList(attrArr);
            ckRelationList.add(tempRel);

        }

        for (Relation ckRel: ckRelationList
             ) {
            decomposedRelations.add(ckRel);
        }


        for (int i = 0; i < decomposedRelations.size(); i++) {
            Relation dr1 = decomposedRelations.get(i);
            ArrayList<String> attrArr1 = new ArrayList<>();
            for (String s: dr1.getAttributes()
                 ) {
                attrArr1.add(s);
            }
            Collections.sort(attrArr1);
            for (int j = i+1; j < decomposedRelations.size(); j++) {
                Relation dr2 = decomposedRelations.get(j);
                ArrayList<String> attrArr2 = new ArrayList<>();
                for (String s: dr2.getAttributes()
                ) {
                    attrArr2.add(s);
                }
                Collections.sort(attrArr2);
                if(attrArr1.containsAll(attrArr2) && attrArr1.size()!=attrArr2.size()){
                    decomposedRelations.remove(j);
                    j--;
                }
            }
        }

        for (int i = 0; i < decomposedRelations.size(); i++) {
            Relation dr1 = new Relation();
            ArrayList<String> attrArr1 = new ArrayList<>();
            for (String s: dr1.getAttributes()
            ) {
                attrArr1.add(s);
            }
            Collections.sort(attrArr1);
            for (int j = i+1; j < decomposedRelations.size(); j++) {
                Relation dr2 = decomposedRelations.get(j);
                ArrayList<String> attrArr2 = new ArrayList<>();
                for (String s: dr2.getAttributes()
                ) {
                    attrArr2.add(s);
                }
                Collections.sort(attrArr2);

                if(attrArr1.equals(attrArr2)){
                    decomposedRelations.remove(j);
                    j--;
                }
            }
        }
        ArrayList<Relation> dList = decomposedRelations;
        for (int i = 0; i < dList.size(); i++) {
            Relation r = dList.get(i);
            System.out.println("Relation " + (i+1));
            HashSet<String> attrSet = new HashSet<>();
            for (String s: r.getAttributes()
            ) {
                attrSet.add(s);
            }
            System.out.println("Attributes: " + attrSet );


            System.out.println("Functional Dependencies: ");
            for (int j = 0; j < r.depValsList.size(); j++) {
                System.out.println(r.depKeyList.get(j) + "-->" + r.depValsList.get(j));
            }

            r.setRelKeyList(r.getAttributes());
            System.out.println("Candidate Keys are");
            ArrayList<HashSet<String>> relKeyList = r.getRelKeyList();

            for (HashSet<String> key: relKeyList
            ) {
                System.out.println(key);
            }
            System.out.println();
        }




    }


}
