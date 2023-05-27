package com.crc.farming.controller;

import com.crc.farming.pojo.*;
import com.crc.farming.service.ArticleService;
import com.crc.farming.service.OrderService;
import com.crc.farming.service.ProductService;
import com.crc.farming.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/professor_manage")
public class Professor_manageController {
    @Autowired
    ArticleService articleService;
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;

    @RequestMapping("/index.html")
    public String test1(){
        return "professor_manage/index";
    }
    @RequestMapping("/insert.html")
    public String test2(){
        return "professor_manage/insert";
    }

    /**
     * 文章编辑
     * @param r
     * @param model
     * @return
     */
    @RequestMapping("/editor.html")
    public String editor(HttpServletRequest r ,Model model){
        //查询所有文章
        List<Article> articles = articleService.queryAllArticle();
        for (Article article:articles) {
            java.sql.Date resultDate = new java.sql.Date(article.getDate().getTime());
            article.setDate(resultDate);
            String s=article.getContent();
            String substr = "";
            substr=s.substring(0, 100);
            article.setContent(substr);
        }

        model.addAttribute("articles",articles);
        return "professor_manage/editor";
    }
    @RequestMapping("/system.html")
    public String test5(){
        return "professor_manage/system";
    }

    /**
     * 文章删除
     * @param
     * @return
     */
    @GetMapping("/delete/{articleid}")
    public String deleteArticle( @PathVariable("articleid") Integer articleid) {
        System.out.println("正在删除");
        // 在这里编写删除逻辑
        articleService.deleteArticle(articleid);
        return "forward:/professor_manage/editor.html";
    }

    /**
     * 修改文章
     * @param
     * @param
     * @return
     * @throws IOException
     */
    @GetMapping("/revise/{articleid}")
    public String reviseArticle( @PathVariable("articleid") Integer articleid,Model model) {
        System.out.println("正在修改");
        // 在这里编写删除逻辑
//        articleService.deleteArticle(articleid);
        Article article = articleService.queryOneArticle(articleid);

        int user_id = article.getId();
        User userById = userService.findUserById(user_id);
        model.addAttribute("author",userById);
        model.addAttribute("article",article);
        return "forward:/professor_manage/insert.html";
    }

    @RequestMapping("/uploadarticle/{articleid}")
    public String fileUpload(@RequestParam("file") MultipartFile file , HttpServletRequest request,@PathVariable("articleid") Integer articleid) throws IOException {
        String source=request.getParameter("source");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        Integer id =Integer.parseInt(request.getParameter("authorid")) ;
        String content = request.getParameter("content");
      /* System.out.println(source+"="+title+"="+expert_id+"="+content);*/
        String picturesource="articleimage/";

        String uploadFileName = file.getOriginalFilename();
      /*  System.out.println(uploadFileName);*/
        String picture=picturesource+uploadFileName;
       /* System.out.println("图片路径 : "+picture);*/
        InputStream is = file.getInputStream(); //文件输入流
        String realPath="src/main/resources/static/farmer/images/product";
        OutputStream os = new FileOutputStream(new File(realPath,uploadFileName)); //文件输出流
        //读取写出
        int len=0;
        byte[] buffer = new byte[1024];
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
            os.flush();
        }
        os.close();
        is.close();
        if(articleid != -1){
            articleService.deleteArticle(articleid);
        }
        System.out.println("正在添加");
        articleService.addArticle(title,new Date(),source,content,id,picture);
        return "forward:/professor_manage/editor.html";
    }


    @RequestMapping("/nomyorderq")
    public String test6(){
        return "professor_manage/nomyorderq";
    }


    @RequestMapping("/myorderq")
    public String test7(HttpServletRequest request, HttpSession session){
        Integer id=Integer.parseInt(request.getParameter("id"));
        /*System.out.println(id);*/
        String name = userService.findUserById(id).getName();
        List<Ord> ordlist=orderService.findUserOrder(id);
        /*System.out.println(ordlist.isEmpty());*/
        if(ordlist.isEmpty()){
            return "professor_manage/nomyorderq";
        }else {
            List<Order> orderList=new ArrayList<>();
            for (Ord ord:ordlist) {
                Double sumprice=0.0;
                List<Cart> carts =new ArrayList<>();
                List<Ordpro> ordprolist=orderService.findUserOrderProduct(ord.getOrderid());
                java.sql.Date resultDate = new java.sql.Date(ord.getOrdertime().getTime());
                ord.setOrdertime(resultDate);
                for (Ordpro ordpro:ordprolist) {
                    Integer product_num=ordpro.getProduct_num();
                    Double product_price = productService.queryProductById(ordpro.getProduct_id()).getProduct_price();
                    sumprice=sumprice+product_price*product_num;

                    Product product=productService.queryProductById(ordpro.getProduct_id());
                    Cart cart=new Cart(product.getProduct_id(),product.getProduct_image(),product.getProduct_name(),product.getProduct_classification(),product.getProduct_price(),product_num);
                    carts.add(cart);

                }

                Order order=new Order(name,ord.getOrderid(),ord.getOrdertime(),carts,sumprice);
                orderList.add(order);
                session.setAttribute("orderList",orderList);

            }

            return "professor_manage/myorderq.html";
        }


    }




    //根据模糊id查询文章
    @RequestMapping("/findOrderByLikeId")
    public String findOrderByIdName(HttpServletRequest request,HttpSession session, Model m){
        String productornum=request.getParameter("productornum");
        Integer id=Integer.parseInt(request.getParameter("id"));

        boolean numeric = isNumeric(productornum);

        if (numeric==true){
            Integer orderid=Integer.parseInt(productornum);
            /* System.out.println(orderid+"--"+id);*/
            List<Ord> ordList = orderService.findOrderByLikeId(orderid,id);
            if (ordList.isEmpty()){
                String name = userService.findUserById(id).getName();
                List<Ord> ordlist=orderService.findUserOrder(id);
                List<Order> orderList=new ArrayList<>();
                for (Ord ord:ordlist) {
                    Double sumprice=0.0;
                    List<Cart> carts =new ArrayList<>();
                    List<Ordpro> ordprolist=orderService.findUserOrderProduct(ord.getOrderid());
                    java.sql.Date resultDate = new java.sql.Date(ord.getOrdertime().getTime());
                    ord.setOrdertime(resultDate);
                    for (Ordpro ordpro:ordprolist) {
                        Integer product_num=ordpro.getProduct_num();
                        Double product_price = productService.queryProductById(ordpro.getProduct_id()).getProduct_price();
                        sumprice=sumprice+product_price*product_num;

                        Product product=productService.queryProductById(ordpro.getProduct_id());
                        Cart cart=new Cart(product.getProduct_id(),product.getProduct_image(),product.getProduct_name(),product.getProduct_classification(),product.getProduct_price(),product_num);
                        carts.add(cart);
                    }
                    Order order=new Order(name,ord.getOrderid(),ord.getOrdertime(),carts,sumprice);
                    orderList.add(order);
                    session.setAttribute("orderList",orderList);
                }
                m.addAttribute("result","无此订单");
                return "professor_manage/myorderq.html";
            }
            else {
                String name = userService.findUserById(id).getName();
                List<Order> orderList=new ArrayList<>();
                for (Ord ord:ordList) {
                    Double sumprice=0.0;
                    List<Cart> carts =new ArrayList<>();
                    List<Ordpro> ordprolist=orderService.findUserOrderProduct(ord.getOrderid());
                    java.sql.Date resultDate = new java.sql.Date(ord.getOrdertime().getTime());
                    ord.setOrdertime(resultDate);
                    for (Ordpro ordpro:ordprolist) {
                        Integer product_num=ordpro.getProduct_num();
                        Double product_price = productService.queryProductById(ordpro.getProduct_id()).getProduct_price();
                        sumprice=sumprice+product_price*product_num;

                        Product product=productService.queryProductById(ordpro.getProduct_id());
                        Cart cart=new Cart(product.getProduct_id(),product.getProduct_image(),product.getProduct_name(),product.getProduct_classification(),product.getProduct_price(),product_num);
                        carts.add(cart);
                    }
                    Order order=new Order(name,ord.getOrderid(),ord.getOrdertime(),carts,sumprice);
                    orderList.add(order);
                    session.setAttribute("orderList",orderList);
                }
                return "professor_manage/searchmyorderq.html";
            }


        }
        else {
            String name = userService.findUserById(id).getName();
            List<Ord> ordlist=orderService.findUserOrder(id);
            List<Order> orderList=new ArrayList<>();
            for (Ord ord:ordlist) {
                Double sumprice=0.0;
                List<Cart> carts =new ArrayList<>();
                List<Ordpro> ordprolist=orderService.findUserOrderProduct(ord.getOrderid());
                java.sql.Date resultDate = new java.sql.Date(ord.getOrdertime().getTime());
                ord.setOrdertime(resultDate);
                for (Ordpro ordpro:ordprolist) {
                    Integer product_num=ordpro.getProduct_num();
                    Double product_price = productService.queryProductById(ordpro.getProduct_id()).getProduct_price();
                    sumprice=sumprice+product_price*product_num;

                    Product product=productService.queryProductById(ordpro.getProduct_id());
                    Cart cart=new Cart(product.getProduct_id(),product.getProduct_image(),product.getProduct_name(),product.getProduct_classification(),product.getProduct_price(),product_num);
                    carts.add(cart);
                }
                Order order=new Order(name,ord.getOrderid(),ord.getOrdertime(),carts,sumprice);
                orderList.add(order);
                session.setAttribute("orderList",orderList);
            }
            m.addAttribute("result","无此订单");
            return "professor_manage/myorderq.html";
        }

    }




    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }return true;
    }


}
