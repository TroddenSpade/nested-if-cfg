package kntu.ep1x;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        ArrayList<Node> list = new ArrayList<Node>();
        String[] cfgString = fileToString("input.txt");
        startDiversion(cfgString,list);

    }

    public static String[] fileToString(String address){
        String code = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(address));
            String line = reader.readLine();
            while (line != null) {
                code += line.replaceAll("\\s+","");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("One Line Code : " + code);
        code = code.replaceAll("\\{","{@");
        code = code.replaceAll("\\}","}@");


        return code.split("[\\;@]");
    }

    public static void startDiversion(String[] string,ArrayList<Node> list){
        System.out.println("<S>");
        if(string.length == 0 || string[0].length() == 0){
            System.out.println("[epsilon]");
        }else{
            list.add(new Node(string,false,Type.BLOCK));
        }
        rec(list);
    }

    public static void rec(ArrayList<Node> list){
        ArrayList<Node> newList = new ArrayList<>();
        for(Node n : list){
            if(n.isLeaf()){
                newList.add(n);
            }else{
                switch (n.getType()){
                    case BLOCK:
                        blockDiversion(newList,n);
                        break;
                    case CONDITION:
                        conditionDiversion(newList,n);
                        break;
                    case IF:
                    case ELSE:
                        if_else_Diversion(newList,n);
                        break;
                    case ELSEIF:
                        elseifDiversion(newList,n);
                        break;
                    default:
                }
            }
        }

        int c = 0;
        for(Node n : list){
            System.out.print(n.toString()+"  ");
            if(!n.isLeaf())  c++;
        }
        System.out.println();
        if(c!=0)
            rec(newList);
    }

    public static void blockDiversion(ArrayList<Node> list,Node node){
        if(node.getBody() == null){
            list.add(new Node(true,Type.EPSILON));
            return;
        }
        String str = node.getBody()[0];
        if(isIf(str)){
            int end = endOfCondition(node.getBody());
            list.add(new Node(
                    Arrays.copyOfRange(node.getBody(),0,end)
                    ,false,Type.CONDITION));
            if(end != node.getBody().length)
                list.add(new Node(
                    Arrays.copyOfRange(node.getBody(),end,node.getBody().length)
                    ,false,Type.BLOCK));
            else
                list.add(new Node(false,Type.BLOCK));
        }else{
            list.add(new Node(
                    Arrays.copyOfRange(node.getBody(),0,1)
                    ,true,Type.COMMAND));
            if(node.getBody().length != 1)
                list.add(new Node(
                    Arrays.copyOfRange(node.getBody(),1,node.getBody().length)
                    ,false,Type.BLOCK));
            else
                list.add(new Node(false,Type.BLOCK));
        }
    }

    public static void elseifDiversion(ArrayList<Node> list,Node node){
        if(node.getBody() == null){
            list.add(new Node(true,Type.EPSILON));
            return;
        }
        String[] strs = node.getBody();
        int end = endOfBrace(strs,0);

        list.add(new Node(Arrays.copyOfRange(node.getBody(),0,1),true,Type.COMMAND));
        if(end > 2){
            list.add(new Node(Arrays.copyOfRange(node.getBody(),1,end-1),false,Type.BLOCK));
        }else{
            list.add(new Node(false,Type.BLOCK));
        }
        list.add(new Node(Arrays.copyOfRange(node.getBody(),end-1,end),true,Type.COMMAND));

        if(strs.length>end && isElif(strs[end])){
            list.add(new Node(Arrays.copyOfRange(node.getBody(),end,strs.length),false,Type.ELSEIF));
        }else
            list.add(new Node(false,Type.ELSEIF));

    }

    public static void if_else_Diversion(ArrayList<Node> list,Node node){
        if(node.getBody() == null){
            list.add(new Node(true,Type.EPSILON));
            return;
        }
        int length = node.getBody().length;
        list.add(new Node(Arrays.copyOfRange(node.getBody(),0,1),true,Type.COMMAND));
        if(length > 2){
            list.add(new Node(Arrays.copyOfRange(node.getBody(),1,length-1),false,Type.BLOCK));

        }else{
            list.add(new Node(false,Type.BLOCK));
        }
        list.add(new Node(Arrays.copyOfRange(node.getBody(),length-1,length),true,Type.COMMAND));

    }


    public static void conditionDiversion(ArrayList<Node> list,Node node){
        String[] strs = node.getBody();
        int end = endOfBrace(strs,0);

        list.add(new Node(Arrays.copyOfRange(node.getBody(),0,end),false,Type.IF));

//        System.out.println(Arrays.toString(Arrays.copyOfRange(node.getBody(),0,end)));

        int start_elseif = end;
        if(end < strs.length && isElif(strs[end])){
            while(end < strs.length && isElif(strs[end])){
                end = endOfBrace(strs,end);
            }
            list.add(new Node(Arrays.copyOfRange(node.getBody(),start_elseif,end),false,Type.ELSEIF));

        }else
            list.add(new Node(false,Type.ELSEIF));



        if(end < strs.length && isElse(strs[end])){
            list.add(new Node(Arrays.copyOfRange(node.getBody(),end,endOfBrace(strs,end)),false,Type.ELSE));
        }else{
            list.add(new Node(false,Type.ELSE));
        }


    }

    public static int endOfBrace(String[] str,int start){
        int i = start;
        int c = 0;
        do{
            if(str[i].contains("{"))
                c++;
            if(str[i].contains("}"))
                c--;
            i++;
        }while (i<str.length && c!=0);

        return i;
    }

    public static int endOfCondition(String[] str){
        int i = 0;
        int c = 0;
        do{
            if(str[i].contains("{"))
                c++;
            if(str[i].contains("}"))
                c--;
            i++;
        }while (i<str.length && (c!=0 || isElif(str[i]) || isElse(str[i])) );

        return i;
    }


    public static boolean isIf(String str){
        if(str.length()>5 && str.substring(0,3).equals("if(")){
            return true;
        }
        return false;
    }
    public static boolean isElif(String str){
        if(str.length()>7 && str.substring(0,7).equals("elseif(")){
            return true;
        }
        return false;
    }
    public static boolean isElse(String str){
        if(str.length()==5 && str.substring(0,5).equals("else{")){
            return true;
        }
        return false;
    }

}
