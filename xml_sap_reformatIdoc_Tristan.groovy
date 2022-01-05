/* @data
<?xml version='1.0' encoding='UTF-8'?>
<ZHR_HRMDA09_EXTN>
	<IDOC BEGIN="1">
		<EDI_DC40 SEGMENT="1">
			<TABNAM>EDI_DC40</TABNAM>
			<IDOCTYP>HRMD_A09</IDOCTYP>
			<CIMTYP>ZHR_HRMDA09_EXTN</CIMTYP>
			<MESTYP>HRMD_A</MESTYP>
			<SNDPOR>SAPS4D</SNDPOR>
			<SNDPRT>LS</SNDPRT>
			<SNDPRN>S4DCLNT200</SNDPRN>
			<RCVPOR>HRMD_IDOCS</RCVPOR>
			<RCVPRT>LS</RCVPRT>
			<RCVPRN>DE4CLNT200</RCVPRN>
			<CREDAT>20200430</CREDAT>
			<CRETIM>152600</CRETIM>
		</EDI_DC40>
		<E1PLOGI SEGMENT="1">
			<PLVAR>01</PLVAR>
			<OTYPE>P</OTYPE>
			<OPERA>I</OPERA>
			<E1PITYP SEGMENT="1">
				<E1P0002 SEGMENT="1">
					<PERNR>00007010</PERNR>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<NACHN>Cypesa</NACHN>
					<VORNA>Cavoz</VORNA>
					<GESCH>1</GESCH>
					<GBDAT>19000101</GBDAT>
					<NATIO/>
					<SPRSL>E</SPRSL>
					<FAMST>6</FAMST>
					<PERID/>
					<E1Q0002 SEGMENT="1">
						<Z1P002 SEGMENT="1" />
					</E1Q0002>
				</E1P0002>
				<Z1P9110 SEGMENT="1">
					<SUBTY>ATS</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<JOB_LEVEL>P4</JOB_LEVEL>
				</Z1P9110>
				<E1P0000 SEGMENT="1">
					<PERNR>00007010</PERNR>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<MASSN>Z2</MASSN>
					<MASSG>1</MASSG>
					<STAT1>2</STAT1>
				</E1P0000>
				<Z1P9013 SEGMENT="1">
					<OTHER_ID>PE85984</OTHER_ID>
				</Z1P9013>
				<E1P0006 SEGMENT="1">
					<PERNR>00007010</PERNR>
					<SUBTY>1</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<ANSSA>1</ANSSA>
					<POSTA/>
					<COM01/>
					<NUM01/>
				</E1P0006>
				<E1P0006 SEGMENT="1">
					<PERNR>00007010</PERNR>
					<SUBTY>1L</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<ANSSA>1L</ANSSA>
					<ORT02/>
					<POSTA/>
					<COM01/>
					<NUM01/>
				</E1P0006>
			</E1PITYP>
		</E1PLOGI>
		<E1PLOGI SEGMENT="1">
			<PLVAR>01</PLVAR>
			<OTYPE>P</OTYPE>
			<OPERA>I</OPERA>
			<E1PITYP SEGMENT="1">
				<E1P0002 SEGMENT="1">
					<PERNR>00009999</PERNR>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<NACHN>Cypesa</NACHN>
					<VORNA>Cavoz</VORNA>
					<GESCH>1</GESCH>
					<GBDAT>19000101</GBDAT>
					<NATIO/>
					<SPRSL>E</SPRSL>
					<FAMST>6</FAMST>
					<PERID/>
					<E1Q0002 SEGMENT="1">
						<Z1P002 SEGMENT="1" />
					</E1Q0002>
				</E1P0002>
				<Z1P9110 SEGMENT="1">
					<SUBTY>ATS</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<JOB_LEVEL>P4</JOB_LEVEL>
				</Z1P9110>
				<E1P0000 SEGMENT="1">
					<PERNR>00009999</PERNR>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<MASSN>Z2</MASSN>
					<MASSG>1</MASSG>
					<STAT1>2</STAT1>
				</E1P0000>
				<Z1P9013 SEGMENT="1">
					<OTHER_ID>PE85984</OTHER_ID>
				</Z1P9013>
				<E1P0006 SEGMENT="1">
					<PERNR>00009999</PERNR>
					<SUBTY>1</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<ANSSA>1</ANSSA>
					<POSTA/>
					<COM01/>
					<NUM01/>
				</E1P0006>
				<E1P0006 SEGMENT="1">
					<PERNR>00009999</PERNR>
					<SUBTY>1L</SUBTY>
					<ENDDA>99991231</ENDDA>
					<BEGDA>19920625</BEGDA>
					<ANSSA>1L</ANSSA>
					<ORT02/>
					<POSTA/>
					<COM01/>
					<NUM01/>
				</E1P0006>
			</E1PITYP>
		</E1PLOGI>
	</IDOC>
</ZHR_HRMDA09_EXTN>
*/

import java.util.Properties;
import java.io.InputStream;
import groovy.util.XmlSlurper;
import groovy.xml.XmlUtil;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    def xs = new XmlSlurper();
    String stDoc = is.getText();
    def xsDoc = xs.parseText(stDoc);
    def xsOut = xs.parseText(stDoc);
    xsOut.IDOC.E1PLOGI.E1PITYP.replaceNode{};

    xsDoc.IDOC.E1PLOGI.each{ logi ->
        def E1PITYPs = []; //new array for each worker (E1PITYP) to keep track of what is processed
        xsDoc.IDOC.E1PLOGI.E1PITYP.'*'.each{el ->
            def elProcessed = E1PITYPs.find { it == el.name() } //check if already processed
            if(elProcessed == null){
                E1PITYPs.push(el.name()); //add to array to keep track
                def elToCopy = logi.E1PITYP.'*'.findAll { it.name() == el.name() } //find all matching elements to current
                xsOut.IDOC.E1PLOGI.appendNode {
                    E1PITYP(elToCopy); //append node
                }
            }
        };
    };

    
    is = new ByteArrayInputStream(XmlUtil.serialize(xsOut).getBytes('UTF-8'));
    dataContext.storeStream(is, props);
}       
