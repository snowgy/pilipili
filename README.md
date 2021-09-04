# PiliPili
![apilevel](https://img.shields.io/badge/API%20level-27-brightgreen.svg) ![springboot](https://img.shields.io/badge/springboot-2.0.6-blue.svg)

<!-- <div align=center>
  <img width = '150' height ='150' src ="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dujgreg7j2074074wel.jpg"/>
</div> -->

</br>

**Pilipili** is an image processing app, which can apply artistic styles to photos. Here are 26 different styles from Van Gogh's Starry Night to Picasso's Les Femmes d’Alger and 6 different image filters. We also build a simple photo community so that users can upload and share their own creative work.



## Info

`This project is only for learning purpose. Welcome to find crash and give an issue.` 

We release both the [app code](<https://github.com/snowgy/pilipili/tree/master/AppClient/PiliPili>) and [server code](<https://github.com/snowgy/pilipili/tree/master/AppClient/PiliPili>). And our server is using `Springboot` framework.

<!-- ## Result

<p float="left">
  <img src="https://ws1.sinaimg.cn/mw690/74c2bf2dgy1g3dwxh1h4hj20tr1nu1kx.jpg" width="300"/>
  <img src="https://ws1.sinaimg.cn/mw690/74c2bf2dgy1g3dwydk90ij20tr1nyk86.jpg" width="300"/>
</p>

<p float="left">
  <img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dvhl44ihg20dc0rs7wi.gif" width="255" />
  <img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dvrkldr9g20h40zknjg.gif" width="255" /> 
  <img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dwrpjafag20dc0rs4qp.gif" width="255"/>
</p>
 -->

## Download

You can download the apk file [here](https://github.com/snowgy/pilipili/blob/master/pilipili.apk)

## Source code Deploy

#### Server Start

1. Enter the server directory

```powershell
$ git clone https://github.com/snowgy/pilipili
$ cd pilipili/Server/pilipili
```

2. Open the `pom.xml` in idea to import the backend project. Then run the springboot application.

3. Modify the `application.properties`. Please change the following attributes.

   ```python
   spring.jpa.hibernate.ddl-auto=create # after the first start, change it to update
   spring.datasource.url="your data source url"
   spring.datasource.username="username"
   spring.datasource.password="password"
   # File Upload
   file.staticAccessPath=/api/file/**
   file.uploadFolder="your local folder to hold the images"
   ```

4. Run `PilipiliApplication`

#### App Start

1. Enter the `pilipili/AppClient/Pilipili`, open `gradle.build` in android studio

2. Modify `com.example.utils.Data`

   ```java
   public final class Data {
       public static final String baseUrl = "you server base url";
       public static final String imgBaseUrl = "your image access url";
       private Data(){
       }
   }
   ```

3. Install app
