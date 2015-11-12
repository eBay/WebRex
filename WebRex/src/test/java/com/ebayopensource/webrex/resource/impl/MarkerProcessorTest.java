/*
    Copyright [2015-2016] eBay Software Foundation

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ebayopensource.webrex.resource.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Ignore;

public class MarkerProcessorTest {


	   static class MockMarkerHandler implements ResourceDeferProcessor.IMarkerHandler {
	      @Override
	      public boolean handle(StringBuilder sb, String marker) {
	         // convert it to upper case
//	         if ("last".equals(marker)) {
	            sb.append(marker.toUpperCase() + "    ");
//	         }
//	         return true;
	            return true;
	      }

	      @Override
	      public String translateMarker(String marker) {
//	         if ("last".equals(marker)) {
	            return marker.toUpperCase() + "    ";
//	         }
//	         return "";
	      }
	   }

	   private static MockMarkerHandler handler = new MockMarkerHandler();

	   private static String template = "${first} segment here<br><break/>and ${second} one here<br><break/>and ${last} one here. ${anything} else?";
	   private static String longTemplate;
	   
	   static {
	      int len = template.length();
	      while(len < 200000) {
	         longTemplate += template;
	         len = longTemplate.length();
	      }
	   }


	   @Test
	   @Ignore
	   public void testV1() {
		   for(int i = 0; i < 100; i ++) {
		      StringBuilder content = new StringBuilder(longTemplate);
	
		      ResourceDeferProcessor.MarkerParserV4.INSTANCE.parse(content, handler);
		   }
		   
		   long start = System.currentTimeMillis();
	       for(int i = 0; i < 10000; i ++) {
		      StringBuilder content = new StringBuilder(longTemplate);
	
		      ResourceDeferProcessor.MarkerParserV4.INSTANCE.parse(content, handler);
		   }
	       System.out.println("Time:" + (System.currentTimeMillis()-start));
	   }


	   @Test
	   @Ignore
	   public void testV3() {
		   for(int i = 0; i < 100; i ++) {
			      StringBuilder content = new StringBuilder(longTemplate);
		
			      ResourceDeferProcessor.MarkerParserV3.INSTANCE.parse(content, handler);
			   }
			   
			   long start = System.currentTimeMillis();
		       for(int i = 0; i < 10000; i ++) {
			      StringBuilder content = new StringBuilder(longTemplate);
		
			      ResourceDeferProcessor.MarkerParserV3.INSTANCE.parse(content, handler);
			   }
		       System.out.println("Time:" + (System.currentTimeMillis()-start));
	   }

	   private static final String test = "<!DOCTYPE html><html lang=\"en\"><head>\n" +
	            "\t${MARKER,UseScriptTag:-317818705}${MARKER,UseScriptTag:1200684157}${MARKER,UseScriptTag:2057095828}${MARKER,UseScriptTag:686738016}${MARKER,UseScriptTag:-1142147518}${MARKER,UseScriptTag:-1248423952}${MARKER,UseScriptTag:852675990}${MARKER,UseScriptTag:450782548}${MARKER,UseScriptTag:1071433168}${MARKER,UseScriptTag:1354248714}${MARKER,UseScriptTag:2053613630}${MARKER,UseScriptTag:99711011}${MARKER,UseScriptTag:-2050969219}${MARKER,UseScriptTag:846099058}${MARKER,UseScriptTag:111134350}${MARKER,UseScriptTag:1675006886}${MARKER,UseScriptTag:-1410597053}${MARKER,UseScriptTag:-1950245437}${MARKER,UseScriptTag:96396470}${MARKER,UseScriptTag:112370566}${MARKER,UseScriptTag:1279771797}${MARKER,UseScriptTag:286848675}${MARKER,UseScriptTag:744154172}${MARKER,UseScriptTag:-1545481183}${MARKER,UseScriptTag:-1590140232}${MARKER,UseScriptTag:-1385576395}${MARKER,UseScriptTag:-1389932628}${MARKER,UseScriptTag:399277595}${MARKER,UseScriptTag:-415051554}<script>var oPageSiteSpeedInfo = {iST:(new Date()).getTime()};</script>\n" +
	            "<script type=\"text/javascript\">var ABOVEFOLD = {imagesCount : 0, completedAt : null, load : function(source){this.completedAt = new Date().getTime();this.imagesCount++;},time : function(){return this.completedAt;}}; </script><title>nike | eBay</title>\n" +
	            "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
	            "\n" +
	            "<link rel=\"canonical\" href=\"http://www.ebay.com:80/sch/Clothing-Shoes-Accessories-/11450/i.html?_nkw=nike\"></link>\n" +
	            "<meta name=\"title\" content=\"nike | eBay\"></meta>\n" +
	            "\t\t\t\t<meta name=\"keywords\" content=\"nike\"></meta>\n" +
	            "\t\t\t\t<meta name=\"description\" content=\"Find nike and adidas from a vast selection of Clothing, Shoes &amp; Accessories. Get great deals on eBay!\"></meta>\n" +
	            "\t\t\t\t<!--[if IE]><link/><![endif]-->\n" +
	            "\t${MARKER,UseScriptTag:997802885}${MARKER,SlotTag:css:gh-css}${MARKER,SlotTag:css:common-css}${MARKER,SlotTag:css:page-css}<!--[if lt IE 8]>\n" +
	            "\t${MARKER,SlotTag:css:page-css-LT_IE8}<![endif]-->\n" +
	            "\t\n" +
	            "\t${MARKER,SlotTag:js:head-js}${MARKER,UseScriptTag:-1209537473}${MARKER,UseScriptTag:1200684157}${MARKER,UseScriptTag:1354248714}${MARKER,UseScriptTag:2057095828}${MARKER,UseScriptTag:-2050969219}${MARKER,UseScriptTag:99711011}${MARKER,UseScriptTag:2053613630}${MARKER,UseScriptTag:1266779567}<noscript><style>#gh-shop, #gh-f { display: block; }\n" +
	            "\t\t#gh-cat-box #gh-cat {display:block}\n" +
	            "\t\t</style></noscript>\n" +
	            "\t<style>.IE_7 .dropdownmenu .sel{*zoom:1}.bqrfTxtCont a.rflbl { position:relative; }.touch .itmcdV2:hover .meta{opacity:0}.touch .itmcdV2:hover .lyr{-webkit-box-shadow:none;box-shadow:none;border:1px solid transparent;border-radius:0;z-index:auto;background:transparent}.touch .itmcdV2:hover div.ititle,.touch .itmcdV2:hover div.ititle h4,.touch .itmcdV2:hover div.ititle h3{white-space:nowrap;height:16px;line-height:16px}.touch .itmcdV2:hover div.ititle.twoLnTtl{height:16px}.touch .l-shad:hover{border:1px solid #DDD;}.menuWpr .pmenu .smenu{margin-top:0;}\n" +
	            ".menuWpr .pmenu .bol{bottom: -2px;}.hrmenu {\tmin-height:40px;}.menuWpr .pmenu a.manc{padding:13px;}</style></head><body class=\"sz940\">${MARKER,UseScriptTag:-283934451}${MARKER,UseScriptTag:1119087801}${MARKER,UseScriptTag:490254264}${MARKER,UseScriptTag:-1138632289}${MARKER,UseScriptTag:420225650}${MARKER,UseScriptTag:-891480550}${MARKER,UseScriptTag:-1071286739}${MARKER,UseScriptTag:-282625124}${MARKER,UseScriptTag:430860329}${MARKER,UseScriptTag:1199175280}${MARKER,UseScriptTag:-1913122278}${MARKER,UseScriptTag:1310698683}${MARKER,UseScriptTag:-153877004}${MARKER,UseScriptTag:-964546034}${MARKER,UseScriptTag:-1440388164}${MARKER,UseScriptTag:-1676545593}${MARKER,UseScriptTag:-1458941948}${MARKER,UseScriptTag:-1430312797}${MARKER,UseScriptTag:-130609723}${MARKER,UseScriptTag:366109515}${MARKER,UseScriptTag:304676799}${MARKER,UseScriptTag:182716121}${MARKER,UseScriptTag:1213747894}${MARKER,UseScriptTag:211345272}${MARKER,UseScriptTag:-1725549873}${MARKER,UseScriptTag:1248108587}${MARKER,UseScriptTag:-529093095}${MARKER,UseScriptTag:50278143}${MARKER,UseScriptTag:-1988901043}${MARKER,UseScriptTag:1006650191}${MARKER,UseScriptTag:-427810264}${MARKER,UseScriptTag:115122298}${MARKER,UseScriptTag:-2106320510}${MARKER,UseScriptTag:785522342}${MARKER,UseScriptTag:1966657789}${MARKER,UseScriptTag:964287762}${MARKER,UseScriptTag:-92751202}${MARKER,UseScriptTag:-2121764996}${MARKER,UseScriptTag:139931100}${MARKER,UseScriptTag:-1066294524}${MARKER,UseScriptTag:-1467823588}${MARKER,UseScriptTag:-1589977042}${MARKER,UseScriptTag:2019796609}${MARKER,UseScriptTag:600219088}${MARKER,UseScriptTag:-1057323985}${MARKER,UseScriptTag:-1547968813}${MARKER,UseScriptTag:2133035765}${MARKER,UseScriptTag:-1789512797}${MARKER,UseScriptTag:-166521195}${MARKER,UseScriptTag:382546478}${MARKER,UseScriptTag:2007045742}${MARKER,UseScriptTag:647025658}${MARKER,UseScriptTag:-2107681987}${MARKER,UseScriptTag:1125974884}${MARKER,UseScriptTag:25259754}${MARKER,UseScriptTag:-596840393}${MARKER,UseScriptTag:-1013910938}${MARKER,UseScriptTag:790362648}${MARKER,UseScriptTag:881806229}${MARKER,UseScriptTag:1056412879}${MARKER,UseScriptTag:-1820862097}${MARKER,UseScriptTag:1465081734}${MARKER,UseScriptTag:2029638827}${MARKER,UseScriptTag:536674615}${MARKER,UseScriptTag:-905977775}${MARKER,UseScriptTag:-1644837094}${MARKER,UseScriptTag:1816724796}${MARKER,UseScriptTag:2142386392}${MARKER,UseScriptTag:-1802050576}${MARKER,UseScriptTag:388917777}${MARKER,UseScriptTag:311653244}${MARKER,UseScriptTag:-553220411}${MARKER,UseScriptTag:-1332363674}${MARKER,UseScriptTag:-704765336}${MARKER,UseScriptTag:244265688}${MARKER,UseScriptTag:331978349}${MARKER,UseScriptTag:-1995101167}${MARKER,UseScriptTag:1818125259}${MARKER,UseScriptTag:509602930}${MARKER,UseScriptTag:-1500216432}${MARKER,UseScriptTag:-1491129396}${MARKER,UseScriptTag:1215814931}${MARKER,UseScriptTag:1904518943}${MARKER,UseScriptTag:-492161805}${MARKER,UseScriptTag:-1578669105}${MARKER,UseScriptTag:1128324472}${MARKER,UseScriptTag:-1348275529}${MARKER,UseScriptTag:-2055439886}${MARKER,UseScriptTag:-871654124}${MARKER,UseScriptTag:399250219}${MARKER,UseScriptTag:-1162231360}${MARKER,UseScriptTag:-1007991479}${MARKER,UseScriptTag:-58325941}${MARKER,UseScriptTag:782249924}${MARKER,UseScriptTag:526702376}${MARKER,UseScriptTag:-228102244}<noscript class=\"nojs\">\n" +
	            "\t<div class=\"nojs-msk\"></div>\n" +
	            "\t<div class=\"nojs-msg shdw\">\n" +
	            "\t\t<h3 class=\"header\">Please enable JavaScript </h3>\n" +
	            "\t\t\t<p>Our new search experience requires JavaScript to be enabled. Please <a href=\"http://enable-javascript.com/\" target=\"_blank\">enable JavaScript on your browser</a>, then <a href=\"http://127.0.0.1:8080/sch/i.html?_nkw=nike&amp;_sacat=11450&amp;_assemblerAppName=dev\">try again</a>.</p>\n" +
	            "\t\t\t<p>To use our basic experience which does not require JavaScript, <a href=\"http://127.0.0.1:8080/sch/i.html?_nkw=nike&amp;_sacat=11450&amp;_assemblerAppName=dev&_jsoff=1\">click here</a>.</p>\n" +
	            "\t</div>\n" +
	            "</noscript>\n" +
	            "<div id=\"Head\"></div><div id=\"Body\" itemscope itemtype=\"http://schema.org/SearchResultsPage\" class=\"VR UNKNOWN UNKNOWN_-1 UNKNOWN_-1_-1\">\n" +
	            "<div id=\"Top\"><div id=\"TopPanel\"><div id=\"TopPanelDF\"><div class=\"gh-acc-exp-div\"><a id=gh-hdn-stm class=\"gh-acc-a\" href=\"#mainContent\">Skip to main content</a></div><div id=gh class=\"gh-flex gh-w gh-site-0\"><style>.gh-qaMsg:hover{opacity:1!important;}</style><sup class=gh-qaMsg style=\"position:absolute; left:37px; z-index:0; top:30px; opacity:0.2; font-size:11px;\"><font color=red>GH3</font> <font color=blue>build </font><font color=green>Sprint 43g</font> <em>(<b id=gh-qaMsgFlag>no </b>Global CSS)</em></sup><table class=gh-tbl><tr><td class=gh-td><a id=\"gh-la\" _sp=\"m570.l2586\" class=\"iclg\" href=\"http://www.qa.ebay.com\">eBay<img width=117 height=120 id=gh-logo src=\"http://ir.ebaystatic.com/pictures/aw/pics/globalheader/spr14.png\" alt=\"\" style=\"clip:rect(47px, 118px, 95px, 0px); position:absolute; top:-47px;left:0\"></a></td><td class=gh-td><div id=gh-shop><a id=\"gh-shop-a\" href=\"http://www.qa.ebay.com/sch/allcategories/all-categories?_trksid=m570.l3694\">Shop by<br>category<i id=gh-shop-ei></i></a></div></td><td class=gh-td-s><form action=\"http://127.0.0.1:8080/sch/i.html?_assemblerAppName=dev\" method=get id=gh-f><input name=\"_odkw\" value=\"nike\" type=\"hidden\"/><input name=\"_assemblerAppName\" value=\"dev\" type=\"hidden\"/><input name=\"_osacat\" value=\"11450\" type=\"hidden\"/><input type=hidden value=m570.l3201 name=_trksid><table class=gh-tbl2><tr><td class=gh-td-s><div id=gh-ac-box><div id=gh-ac-box2><label class=\"gh-hdn g-hdn\" for=\"gh-ac\">Enter your search keyword</label><input autocomplete=off name=\"_nkw\" id=gh-ac placeholder=\"Search... \" maxlength=300 size=50 class=gh-tb type=text  value=\"nike\"></div></div></td><td class=gh-td id=gh-cat-td><div id=gh-cat-box><select name=_sacat id=gh-cat size=1 class=gh-sb title=\"Select a category for search\"><option value=\"0\">All Categories</option><optgroup label=\"----------------------------------------\"></optgroup><option value=\"20081\">Antiques</option><option value=\"550\">Art</option><option value=\"2984\">Baby</option><option value=\"267\">Books</option><option value=\"12576\">Business &amp; Industrial</option><option value=\"625\">Cameras &amp; Photo</option><option value=\"15032\">Cell Phones &amp; Accessories</option><option value=\"11450\" selected=\"selected\">Clothing, Shoes &amp; Accessories</option><option value=\"11116\">Coins &amp; Paper Money</option><option value=\"1\">Collectibles</option><option value=\"58058\">Computers/Tablets &amp; Networking</option><option value=\"293\">Consumer Electronics</option><option value=\"14339\">Crafts</option><option value=\"237\">Dolls &amp; Bears</option><option value=\"11232\">DVDs &amp; Movies</option><option value=\"6000\">eBay Motors</option><option value=\"45100\">Entertainment Memorabilia</option><option value=\"172008\">Gift Cards &amp; Coupons</option><option value=\"26395\">Health &amp; Beauty</option><option value=\"11700\">Home &amp; Garden</option><option value=\"281\">Jewelry &amp; Watches</option><option value=\"11233\">Music</option>"
	            + "<link href=\"/ressvr/v/kcmyo3okh22p3po1zhmhfqc12ym.css?debug=true&showRaw=true\" type=\"text/css\" rel=\"stylesheet\"><div id=\"t_LeftCenterBottomPanelDF\" style=\"display:none\"><div id=\"LeftCenterBottomPanelDF\"><script type=\"text/javascript\">$df.set('ph_LeftCenterBottomPanelDF','LeftCenterBottomPanelDF')</script><div id=\"Left\">\n" +
	            "\t<div id=\"LeftPanel\">\n" +
	            "\t\t<h2 class=\"hdnHdr\">Search refinements</h2>\n" +
	            "\t\t${MARKER,UseScriptTag:1966657789}<div id=\"DashLeftPanel\" class=\"lnav\">\n" +
	            "\t\t\t\t\t${MARKER,UseScriptTag:1119087801}<div id=\"LeftNavContainer\" class=\"lnav\">\n" +
	            "\t\t\n" +
	            "\t\t${MARKER,UseScriptTag:-1430212211}<div  class=\"asp pnl\" _sp=\"p0.m1684_format\">\n" +
	            "\t<div class=\"pnl-h frmt_head\">\n" +
	            "\t\t<a class=\"more\" id=\"fmore\" href=\"javascript:;\" ismore=\"true\">more<span class=\"hdn\">Format</span></a><span class=\"pnl-h\"><h3>Format</h3></span>\n" +
	            "\t</div>\n" +
	            "\t<div class=\"pnl-b frmt\">\n" +
	            "\t\t<span class=\"sel tgl_button first_b\">All Listings</span>\n" +
	            "\t\t\t\t\t<a  href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;rt=nc&amp;LH_Auction=1\" class=\"cbx btn btn-s btn-ter tab tgl_button center_b\">Auction</a>\n" +
	            "\t\t\t\t\t<a  href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;rt=nc&amp;LH_BIN=1\" class=\"cbx btn btn-s btn-ter tab tgl_button last_b\">Buy It Now</a>\n" +
	            "\t\t\t\t\t<a href=\"\" class=\"cbx btn btn-s btn-ter tab\" id=\"f_LH_CAds\" style=\"display:none\">Classified ads</a>\n" +
	            "\t\t</div>\n" +
	            "</div>\n" +
	            "<script type=\"text/javascript\">\n" +
	            "\tvar FA = raptor.require('dash.aspects.FormatAspect');\n" +
	            "\tnew FA({'content':{'more':'more', 'less': 'less'}, 'classifiedClicked':false });\n" +
	            "</script><div class=\"lct-lnks\">\n" +
	            "\t\t\t${MARKER,UseScriptTag:388917777}${MARKER,UseScriptTag:340072611}<div id=\"LeftNavCategoryContainer\">\n" +
	            "<div  id=\"e1-1\" class=\"cat rlp\" _sp=\"p0.m1685\">\n" +
	            "\t<input type=\"hidden\" id=\"domCatId\" value=\"11450\"></input>\n" +
	            "\t<div class=\"rlp-h\"><b class=\"srpArwExpandCollapse2 rlp-i\"></b><h3>Categories</h3></div>\n" +
	            "\t<div class=\"rlp-b\">\n" +
	            "\t\t<div id=\"CategoriesGroupContainer\" class=\"catsgroup\">\n" +
	            "\t\t\t<div class=\"cat-t\"><a href=\"http://localhost:8080/sch/Clothing-Shoes-Accessories-/11450/i.html?_nkw=nike\"_sp=\"p0.m1685.c1\" >Clothing, Shoes &amp; Accessories</a><span class=\"cnt\">&nbsp;(721,100)</span></div>\n" +
	            "\t\t\t<div class=\"cat-c\">\n" +
	            "\t\t\t\t<div class=\"default\">\n" +
	            "\t\t\t\t\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Mens-Shoes-/93427/i.html?_nkw=nike\" _sp=\"p0.m1685.c2\" >Men&#039;s Shoes</a><span class=\"cnt\">&nbsp;(327,602)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Womens-Shoes-/3034/i.html?_nkw=nike\" _sp=\"p0.m1685.c3\" >Women&#039;s Shoes</a><span class=\"cnt\">&nbsp;(74,771)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Mens-Clothing-/1059/i.html?_nkw=nike\" _sp=\"p0.m1685.c4\" >Men&#039;s Clothing</a><span class=\"cnt\">&nbsp;(131,214)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Womens-Clothing-/15724/i.html?_nkw=nike\" _sp=\"p0.m1685.c5\" >Women&#039;s Clothing</a><span class=\"cnt\">&nbsp;(60,980)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Kids-Clothing-Shoes-Accs-/171146/i.html?_nkw=nike\" _sp=\"p0.m1685.c6\" >Kids&#039; Clothing, Shoes &amp; Accs</a><span class=\"cnt\">&nbsp;(80,190)</span></div>\n" +
	            "\t\t</div>\n" +
	            "\t\t\t\t<div class=\"optional hide\">\n" +
	            "\t\t\t\t\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Baby-Toddler-Clothing-/3082/i.html?_nkw=nike\" _sp=\"p0.m1685.c1\" >Baby &amp; Toddler Clothing</a><span class=\"cnt\">&nbsp;(27,331)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Mens-Accessories-/4250/i.html?_nkw=nike\" _sp=\"p0.m1685.c2\" >Men&#039;s Accessories</a><span class=\"cnt\">&nbsp;(8,011)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Unisex-Clothing-Shoes-Accs-/155184/i.html?_nkw=nike\" _sp=\"p0.m1685.c3\" >Unisex Clothing, Shoes &amp; Accs</a><span class=\"cnt\">&nbsp;(12,264)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Womens-Handbags-Bags-/169291/i.html?_nkw=nike\" _sp=\"p0.m1685.c4\" >Women&#039;s Handbags &amp; Bags</a><span class=\"cnt\">&nbsp;(201)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Vintage-/175759/i.html?_nkw=nike\" _sp=\"p0.m1685.c5\" >Vintage</a><span class=\"cnt\">&nbsp;(1,442)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Womens-Accessories-/4251/i.html?_nkw=nike\" _sp=\"p0.m1685.c6\" >Women&#039;s Accessories</a><span class=\"cnt\">&nbsp;(1,310)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Wholesale-Large-Small-Lots-/41964/i.html?_nkw=nike\" _sp=\"p0.m1685.c7\" >Wholesale, Large &amp; Small Lots</a><span class=\"cnt\">&nbsp;(69)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Uniforms-Work-Clothing-/28015/i.html?_nkw=nike\" _sp=\"p0.m1685.c8\" >Uniforms &amp; Work Clothing</a><span class=\"cnt\">&nbsp;(24)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Cultural-Ethnic-Clothing-/155240/i.html?_nkw=nike\" _sp=\"p0.m1685.c9\" >Cultural &amp; Ethnic Clothing</a><span class=\"cnt\">&nbsp;(18)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Dancewear-/112425/i.html?_nkw=nike\" _sp=\"p0.m1685.c10\" >Dancewear</a><span class=\"cnt\">&nbsp;(16)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Wedding-Formal-Occasion-/3259/i.html?_nkw=nike\" _sp=\"p0.m1685.c11\" >Wedding &amp; Formal Occasion</a><span class=\"cnt\">&nbsp;(7)</span></div>\n" +
	            "\t\t<div class=\"cat-link\" id=\"cat-dash\"><a  href=\"http://localhost:8080/sch/Costumes-Reenactment-Theater-/163147/i.html?_nkw=nike\" _sp=\"p0.m1685.c12\" >Costumes, Reenactment, Theater</a><span class=\"cnt\">&nbsp;(4)</span></div>\n" +
	            "\t\t</div>\n" +
	            "\t\t\t\t\t<a href=\"#\" class=\"cat-sh\" onclick=\"return false;\"><span class=\"cat-more\">More</span><span class=\"icon\"></span></a>\n" +
	            "\t\t\t\t\t\t\t\t</div>\n" +
	            "\t\t</div>\n" +
	            "\t<div class=\"catsgroup\">\n" +
	            "\t\t\t\t\t<a class=\"cat-t\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_sac=1#seeAllAnchorLink\">See all categories</a>\n" +
	            "\t\t\t\t</div>\t\t\t\n" +
	            "\t\t\t</div>\n" +
	            "</div>\n" +
	            "</div>\n" +
	            "${MARKER,UseScriptTag:-559229949}</div>\n" +
	            "\t${MARKER,UseScriptTag:-1425243212}${MARKER,UseScriptTag:-729943508}<div id=\"e1-22\" class=\"asp\" _sp=\"p0.m1684\">\n" +
	            "\t<div  id=\"e1-23\" class=\"pnl\">\n" +
	            "\t<div class=\"pnl-h\">\n" +
	            "\t\t<a class=\"more\" id=\"bluelink\" href=\"javascript:;\">see all<span class=\"hdn\">Format</span></a><span class=\"pnl-h\"><h3>Format</h3></span>\n" +
	            "\t</div>\n" +
	            "\t<div class=\"pnl-b\">\n" +
	            "\t\t<div   id=\"e1-24\" class=\"rbx \"  >\n" +
	            "\t\t\t\t<a   class=\"rbx \"  href=\"http://localhost:8080/sch/i.html?_nkw=nike\"    title=\"All Listings\">\n" +
	            "\t\t\t\t\t<input type=\"radio\" autocomplete=\"off\"  name=\"LH_BuyingFormats\" id=\"e1-25\" class=\"rbx \" checked />\n" +
	            "\t\t\t\t\t<label for=\"e1-25\"><span class=\"rbx \">All Listings</span></label>\n" +
	            "\t\t\t\t</a>\n" +
	            "\t\t\t\t</div>\n" +
	            "\t\t\t<div   id=\"e1-26\" class=\"rbx \"  >\n" +
	            "\t\t\t\t<a   class=\"rbx \"  href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;rt=nc&amp;LH_Auction=1\"    title=\"Auction\">\n" +
	            "\t\t\t\t\t<input type=\"radio\" autocomplete=\"off\"  name=\"LH_BuyingFormats\" id=\"e1-27\" class=\"rbx \"  />\n" +
	            "\t\t\t\t\t<label for=\"e1-27\"><span class=\"rbx \">Auction</span></label>\n" +
	            "\t\t\t\t</a>\n" +
	            "\t\t\t\t</div>\n" +
	            "\t\t\t<div   id=\"e1-28\" class=\"rbx \"  >\n" +
	            "\t\t\t\t<a   class=\"rbx \"  href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;rt=nc&amp;LH_BIN=1\"    title=\"Buy It Now\">\n" +
	            "\t\t\t\t\t<input type=\"radio\" autocomplete=\"off\"  name=\"LH_BuyingFormats\" id=\"e1-29\" class=\"rbx \"  />\n" +
	            "\t\t\t\t\t<label for=\"e1-29\"><span class=\"rbx \">Buy It Now</span></label>\n" +
	            "\t\t\t\t</a>\n" +
	            "\t\t\t\t</div>\n" +
	            "\t\t\t</div>\n" +
	            "</div><div  id=\"e1-30\" class=\"pnl\">\n" +
	            "\t<div class=\"pnl-h\">\n" +
	            "\t    <a class=\"more\" id=\"bluelink\" href=\"javascript:;\">see all<span class=\"hdn\">US Shoe Size (Men's)</span></a><span class=\"pnl-h\"><h3>US Shoe Size (Men's)</h3></span>\n" +
	            "\t</div>\n" +
	            "\t<div class=\"pnl-b\">\n" +
	            "\t\t<div id=\"e1-31\" class=\"cbx\">\n" +
	            "\t\t\t\t\t\t<a  class=\"cbx\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_dcat=15709&amp;US%2520Shoe%2520Size%2520%2528Men%2527s%2529=9%252E5&amp;rt=nc\" title=\"9.5\">\n" +
	            "\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"US%20Shoe%20Size%20%28Men%27s%29\" id=\"e1-32\" class=\"cbx\"  />\n" +
	            "\t\t\t\t\t\t\t<label for=\"e1-32\"><span class=\"cbx\">9.5</span></label>\n" +
	            "\t\t\t\t\t\t</a>\n" +
	            "\t\t\t\t\t\t&nbsp;<span class=\"cnt\">(30,886)</span></div>\n" +
	            "\t\t\t\t<div id=\"e1-33\" class=\"cbx\">\n" +
	            "\t\t\t\t\t\t<a  class=\"cbx\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_dcat=15709&amp;US%2520Shoe%2520Size%2520%2528Men%2527s%2529=10&amp;rt=nc\" title=\"10\">\n" +
	            "\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"US%20Shoe%20Size%20%28Men%27s%29\" id=\"e1-34\" class=\"cbx\"  />\n" +
	            "\t\t\t\t\t\t\t<label for=\"e1-34\"><span class=\"cbx\">10</span></label>\n" +
	            "\t\t\t\t\t\t</a>\n" +
	            "\t\t\t\t\t\t&nbsp;<span class=\"cnt\">(35,740)</span></div>\n" +
	            "\t\t\t\t<div id=\"e1-35\" class=\"cbx\">\n" +
	            "\t\t\t\t\t\t<a  class=\"cbx\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_dcat=15709&amp;US%2520Shoe%2520Size%2520%2528Men%2527s%2529=10%252E5&amp;rt=nc\" title=\"10.5\">\n" +
	            "\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"US%20Shoe%20Size%20%28Men%27s%29\" id=\"e1-36\" class=\"cbx\"  />\n" +
	            "\t\t\t\t\t\t\t<label for=\"e1-36\"><span class=\"cbx\">10.5</span></label>\n" +
	            "\t\t\t\t\t\t</a>\n" +
	            "\t\t\t\t\t\t&nbsp;<span class=\"cnt\">(33,254)</span></div>\n" +
	            "\t\t\t\t<div id=\"e1-37\" class=\"cbx\">\n" +
	            "\t\t\t\t\t\t<a  class=\"cbx\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_dcat=15709&amp;US%2520Shoe%2520Size%2520%2528Men%2527s%2529=11&amp;rt=nc\" title=\"11\">\n" +
	            "\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"US%20Shoe%20Size%20%28Men%27s%29\" id=\"e1-38\" class=\"cbx\"  />\n" +
	            "\t\t\t\t\t\t\t<label for=\"e1-38\"><span class=\"cbx\">11</span></label>\n" +
	            "\t\t\t\t\t\t</a>\n" +
	            "\t\t\t\t\t\t&nbsp;<span class=\"cnt\">(32,128)</span></div>\n" +
	            "\t\t\t\t</div>\n" +
	            "</div>\n" +
	            "<div  id=\"e1-39\" class=\"pnl\">\n" +
	            "\t<div class=\"pnl-h\">\n" +
	            "\t    <a class=\"more\" id=\"bluelink\" href=\"javascript:;\">see all<span class=\"hdn\">Color</span></a><span class=\"pnl-h\"><h3>Color</h3></span>\n" +
	            "\t</div>\n" +
	            "\t<div class=\"pnl-b\">\n" +
	            "\t\t<div id=\"e1-40\" class=\"cbx\">\n" +
	            "\t\t\t\t\t\t<a  class=\"cbx\" href=\"http://localhost:8080/sch/i.html?_nkw=nike&amp;_dcat=15709&amp;Color=Beige&amp;rt=nc\" title=\"Beige\">\n" +
	            "\t\t\t\t\t\t\t";
	   
	   @Test
	   public void testV2() {
		   String template = "${first} segment here      \r\n  <br>   \r\n\t <break/> and ${second} one here<br>    \r\n<break/>   and ${last} one here. ${anything} else?";
		   StringBuilder content = new StringBuilder(template);
			
		      ResourceDeferProcessor.MarkerParserV4.INSTANCE.parse(content, handler);
		      System.out.println(content);
	   }
	   
	   
	   @Test
	   public void testV4() {
		   StringBuilder content = new StringBuilder(test);
			
		      ResourceDeferProcessor.MarkerParserV4.INSTANCE.parse(content, handler);
		      System.out.println(content);
	   }
	   
	   @Test
	   public void testSwitch() {
		   String template = "${first} segment here      \r\n  <br>   \r\n\t <break/> and ${second} one here<br>    \r\n<break/>   and ${last} one here. ${anything} else?";
		   
		   
		   ResourceDeferProcessor processor = new ResourceDeferProcessor(true);
		   StringBuilder sb = new StringBuilder(template);
		   processor.process(sb);
		   
		   assertEquals("${first} segment here      \r\n  <br><break/> and ${second} one here<br><break/>   and ${last} one here. ${anything} else?", sb.toString());
		   
		   
		   processor = new ResourceDeferProcessor(false);
		   sb = new StringBuilder(template);
		   processor.process(sb);
		   
		   assertEquals("${first} segment here      \r\n  <br>   \r\n\t <break/> and ${second} one here<br>    \r\n<break/>   and ${last} one here. ${anything} else?", sb.toString());
			  
	   }
	 
	   

//	   @Test
//	   @Ignore
//	   public void testNewProcessor2() {
//	      for (int i = 15; i <= 1024; i++) {
//	         NewMarkerParser.BUFFER_SIZE = i;
//	         StringBuilder content = new StringBuilder(template);
//	         NewMarkerParser.INSTANCE.parse(content, handler);
//
//	         assertEquals("FIRST    segment here<br><break/>and SECOND    one here<br><break/>and LAST    one here. ANYTHING    else?",
//	               content.toString());
//	      }
//	   }
}
