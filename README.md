# PiliPili


<div align=center>
  <img width = '150' height ='150' src ="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dujgreg7j2074074wel.jpg"/>
</div>



**Pilipili** is an image processing app, which can apply artistic styles to photos. Here are 26 different styles from Van Gogh's Starry Night to Picasso's Les Femmes d’Alger and 6 different image filters. We also build a simple photo community so that users can upload and share their own creative work.



## Info

`This project is only for learning purpose. Welcome to find crash and give an issue.` 

We release both the [app code](<https://github.com/snowgy/pilipili/tree/master/AppClient/PiliPili>)and [server code](<https://github.com/snowgy/pilipili/tree/master/AppClient/PiliPili>). And our server is using `Springboot` framework.

## Result

<div style="float:left;border:solid 1px 000;padding:10px;"><img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dvhl44ihg20dc0rs7wi.gif"  width="300" ></div>
<div style="float:left;border:solid 1px 000;padding:10px;"><img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dvxoypxej2034102t8n.jpg" width="50"/></div>
<div style="float:left;border:solid 1px 000;padding:10px;"><img src="https://ws1.sinaimg.cn/large/74c2bf2dgy1g3dvrkldr9g20h40zknjg.gif" width="300"></div>


## Download

You can download the apk file [here]()

## Deploy

If you want to run our server code, follow the following step to start.

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

