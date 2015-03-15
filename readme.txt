1. DIT
   1.1 To start the utilily, use
   java -cp ojdbc-6.11.1.0.7.0.jar:poi-3.2-FINAL.jar:dbutils-1.0.0.0.jar com.tsoft.dbutils.dit.DIT dit_bs_nightly_perf.xml

   1.2 Input file example
   <?xml version="1.0" encoding="UTF-8" ?>
   <dit>
     <connection name="SNGALFA">
       <driver>oracle.jdbc.OracleDriver</driver>
       <url>jdbc:oracle:thin:@10.130.1.195:1521/SNGOIL2</url>
       <username>read</username>
       <password>all746</password>
       <schema>SNGALFA</schema>
       <DITDriver>com.tsoft.dbutils.dit.driver.oracle.OracleDriver</DITDriver>
       <generate>
         <HTML>
         </HTML>
         <create>
           <exclude name="[\S]+" category="[\S]+"/>
         </create>
         <insert>
           <exclude name="[\S]+" category="[\S]+"/>
         </insert>
       </generate>  
     </connection>
   </dit>

2. DMT
   1.1 RMIC
   rmic.exe -d "c:\project\DatabaseUtils\DMT\classes" -classpath "c:\project\DatabaseUtils\DMT\classes" tsoft.database.dmt.driver.dit.DITDriver
   rmic.exe -d "c:\project\DatabaseUtils\DMT\classes" -classpath "c:\project\DatabaseUtils\DMT\classes" tsoft.database.dmt.driver.cube.CubeDriver

   1.2 Input file example
   <?xml version="1.0" encoding="windows-1251" ?>
   <dmt>
     <connection name="V_2004">
       <driver>com.microsoft.jdbc.sqlserver.SQLServerDriver</driver>
       <url>jdbc:microsoft:sqlserver://ASU:1433;DatabaseName=V_2004</url>
       <username>sa</username>
       <password>12</password>
     </connection>

     <model name="Data" connection="V_2004" DMTDriver="tsoft.database.dmt.driver.dit.DITDriver">
       <DIT file="c:\project\DatabaseUtils\DIT\MSSQL 2000\ASU\V_2004\sa\object.xml"/>
     </model>
  
     <model name="Vyl" connection="V_2004" DMTDriver="tsoft.database.dmt.driver.cube.CubeDriver">
       <cube name="TotalRecordCount" query="select Zak, VidVert, count(*) as cnt from Vyl group by Zak, VidVert">
         <dim axis="Y" field="Zak" query="select Name from Zak where Kod=@Zak"/>
         <dim axis="Y" field="VidVert" query="select Name from VidVert where Kod=@VidVert"/>
         <dim axis="X" field="cnt"/>
       </cube>
       <cube name="QTimeMinValue" query="select Zak, min(QTime) as QTime from Vyl group by Zak">
         <dim axis="Y" field="Zak" query="select Name from Zak where Kod=@Zak"/>
         <dim axis="X" field="QTime"/>
       </cube>
     </model>
   </dmt>

3. DTB
   1.1 To start
   C:\jdk-1_5_0_05\bin\java.exe -ojvm -classpath C:\jprj\DatabaseUtils\DTB\classes;C:\jprj\DatabaseUtils\LIB\classes tsoft.database.dtb.DTB %1

   1.2 Input file example
   <?xml version="1.0" encoding="windows-1251" ?>
   <dtb>
     <build>
       <source>
         <driver>oracle.jdbc.OracleDriver</driver>
         <url>jdbc:oracle:thin:@10.130.1.195:1521:SNGOIL2</url>
         <username>read</username>
         <password>all746</password>
         <DIT>c:\jprj\DatabaseUtils\DIT\Oracle 9i\10.130.1.195\SNGOIL2\SNGALFA\object.xml</DIT>
       </source>
       <dest>
         <driver>oracle.jdbc.OracleDriver</driver>
         <url>jdbc:oracle:thin:@192.168.1.1:1521:ORADB</url>
         <username>SNGALFA</username>
         <password>12</password>
         <DTT>c:\jprj\DatabaseUtils\DTB\dtt_SNGALFA.xml</DTT>
       </dest>
     </build>
   </dtb>

4. DTT
   1.1 To start
   C:\jdk-1_5_0_05\bin\java.exe -ojvm -classpath C:\jprj\DatabaseUtils\DTT\classes;C:\jprj\DatabaseUtils\LIB\classes;C:\jdev\jdbc\lib\ojdbc14dms.jar;C:\jdev\jdbc\lib\orai18n.jar;C:\jdev\jdbc\lib\ocrs12.jar;C:\jdev\diagnostics\lib\ojdl.jar;C:\jdev\lib\dms.jar tsoft.database.dtt.DTT %1

   1.2 Example
   <?xml version="1.0" encoding="windows-1251" ?>
   <dtt>
     <connection name="DB.SRC">
       <driver>oracle.jdbc.OracleDriver</driver>
       <url>jdbc:oracle:thin:@10.130.1.195:1521:SNGOIL2</url>
       <username>read</username>
       <password>all746</password>
     </connection>
     <connection name="DB.DEST">
       <driver>oracle.jdbc.OracleDriver</driver>
       <url>jdbc:oracle:thin:@192.168.1.1:1521:ORADB</url>
       <username>SNGALFA</username>
       <password>12</password>
     </connection>
     <transformation name="Table WELL_D_PROBLEM">
       <connection from="DB.SRC" to="DB.DEST"/>
       <dataset from="WELL_D_PROBLEM" to="WELL_D_PROBLEM" comment="Справочник осложнений на скважинах" create="True"/>
       <copy from="CODPROB" to="CODPROB" nullable="false" primary="true" comment="Код осложнения"/>
       <copy from="PROB_FULL" to="PROB_FULL" nullable="true" comment="Полное описание осложнения"/>
       <copy from="PROB_SHORT" to="PROB_SHORT" nullable="true" comment="Краткое описание осложнения"/>
       <copy from="PRIZ" to="PRIZ" nullable="true" comment="Признак НГДУ"/>
     </transformation>
     <transformation name="Table REMBOT">
       <connection from="DB.SRC" to="DB.DEST"/>
       <dataset from="REMBOT" to="REMBOT" comment="Справочник причин простоев ремонтных бригад" create="True"/>
       <copy from="NUM_REM_BRIG_BOT" to="NUM_REM_BRIG_BOT" nullable="false" primary="true" comment="Код причины простоя"/>
       <copy from="NAME_REM_BRIG_BOT" to="NAME_REM_BRIG_BOT" nullable="true" comment="Наименование причины"/>
       <copy from="NM_REM_BRIG_BOT" to="NM_REM_BRIG_BOT" nullable="true" comment="Краткое наименов. причины"/>
     </transformation>
   </dtt>
