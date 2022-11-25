package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class Crawler {
    private HashSet<String> urllink;
    public Connection connection;

    public Crawler(){

        connection= DatabaseConnection.getConnection();

        urllink = new HashSet<String>();

    }
    public void getPageTextAndLinks(String URL,int depth)   {
        if(!urllink.contains(URL)){
            try{
                if (urllink.add(URL)){
                    System.out.println(URL);
                }
                Document document = Jsoup.connect(URL).userAgent("Chrome").timeout(5000).get();
                String text = document.text().length()<501?document.text():document.text().substring(0,500);
                PreparedStatement preparedStatement = connection.prepareStatement("Insert into pages values(?,?,?)");
                preparedStatement.setString(1,document.title());
                preparedStatement.setString(2,URL);
                preparedStatement.setString(3,text);
                preparedStatement.executeUpdate();

                depth++;
                if(depth==2){
                    return;
                }

                Elements availableLinkOnPage =document.select("a[href");
                for(Element element:availableLinkOnPage){
                    getPageTextAndLinks(element.attr("abs:href"),depth);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        Crawler crawler = new Crawler();
        crawler.getPageTextAndLinks("https://www.javatpoint.com",0);
    }
}