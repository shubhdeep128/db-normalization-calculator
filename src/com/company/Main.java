package com.company;
import com.company.Relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
public class Main {



    public static void main(String[] args) {
		System.out.println("Enter Attributes. Ex: A B C D E");
		Relation r = new Relation();

		String str;
		Scanner sc = new Scanner(System.in);

		str = sc.nextLine();
		r.setAttributes(r.parseAttributes(str));

		System.out.println("Enter Functional Dependencies. Ex: A,B>C,E;A,D>E");
		str = sc.nextLine();
		r.setDependencyList(r.parseDependency(str));
		ArrayList<ArrayList<String>> keys = r.getDepKeyList();
		ArrayList<ArrayList<String>> values = r.getDepValsList();
		System.out.println();
		for (int i = 0; i < keys.size(); i++) {
			StringBuilder key = new StringBuilder();
			for (int j = 0; j < keys.get(i).size(); j++) {
				key.append(keys.get(i).get(j));
			}
			StringBuilder value = new StringBuilder();
			for (int j = 0; j < values.get(i).size(); j++) {
				value.append(values.get(i).get(j));
			}
			System.out.println(key + "--->" + value);
		}
		System.out.println();
		r.setRelKeyList(r.getAttributes());
		System.out.println("Candidate Keys are");
		ArrayList<HashSet<String>> relKeyList = r.getRelKeyList();

		for (HashSet<String> key: relKeyList
		) {
			System.out.println(key);
		}
		System.out.println();
		r.decomposer();
//		System.out.println("The Normal Forms satisfied are: ");
//		System.out.println("2NF = "+r.checkTwoNF());
//		System.out.println("3NF = "+r.checkThreeNF());
//		System.out.println("BCNF = "+r.checkBCNF());
//
//
//		if(r.checkTwoNF()){
//			System.out.println("Decomposition to 3NF");
//			r.TwoToThree();
//		}
//
//		System.out.println("Decomposition to 2NF");
//		r.OneToTwo();

	}
}
