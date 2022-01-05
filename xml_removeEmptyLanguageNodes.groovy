/* @data
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Body>
    <glob:SupplierInvoiceBundleMaintainRequest_sync xmlns:glob="http://sap.com/xi/SAPGlobal20/Global">
      <SupplierInvoice actionCode="01">
        <BusinessTransactionDocumentTypeCode>004</BusinessTransactionDocumentTypeCode>
        <Date>2020-09-11</Date>
        <ReceiptDate>2020-01-04</ReceiptDate>
        <TransactionDate>2020-09-11</TransactionDate>
        <DocumentItemGrossAmountIndicator>true</DocumentItemGrossAmountIndicator>
        <ManualEntryERSIndicator>false</ManualEntryERSIndicator>
        <GrossAmount currencyCode="USD">550.12</GrossAmount>
        <Status>
          <DataEntryProcessingStatusCode>3</DataEntryProcessingStatusCode>
        </Status>
        <CustomerInvoiceReference actionCode="01">
          <BusinessTransactionDocumentReference>
            <ID>324d761e-c799-4_100026168</ID>
            <TypeCode>28</TypeCode>
          </BusinessTransactionDocumentReference>
        </CustomerInvoiceReference>
        <BuyerParty actionCode="01">
          <PartyKey>
            <PartyTypeCode>200</PartyTypeCode>
            <PartyID>AOSpine</PartyID>
          </PartyKey>
        </BuyerParty>
        <SellerParty actionCode="01">
          <PartyKey>
            <PartyTypeCode>147</PartyTypeCode>
            <PartyID>100026168</PartyID>
          </PartyKey>
        </SellerParty>
        <BillToParty actionCode="01">
          <PartyKey>
            <PartyTypeCode>200</PartyTypeCode>
            <PartyID>AOSpine</PartyID>
          </PartyKey>
        </BillToParty>
        <PayeeParty actionCode="01">
          <PartyKey>
            <PartyTypeCode>147</PartyTypeCode>
            <PartyID>100026168</PartyID>
          </PartyKey>
        </PayeeParty>
        <Item actionCode="01">
          <BusinessTransactionDocumentItemTypeCode>002</BusinessTransactionDocumentItemTypeCode>
          <SHORT_Description>EventPerDiem</SHORT_Description>
          <GrossAmount>75</GrossAmount>
          <AccountingCodingBlockDistribution ActionCode="01">
            <GeneralLedgerAccountAliasCode>5050.20</GeneralLedgerAccountAliasCode>
            <TotalAmount>75</TotalAmount>
            <AccountingCodingBlockAssignment ActionCode="01">
              <Amount>75</Amount>
              <GeneralLedgerAccountAliasCode>5050.20</GeneralLedgerAccountAliasCode>
              <ProjectTaskKey>
                <TaskID>AOEEP</TaskID>
              </ProjectTaskKey>
            </AccountingCodingBlockAssignment>
            <EmployeeID>114885</EmployeeID>
          </AccountingCodingBlockDistribution>
          <ProductTax actionCode="01">
            <ProductTaxationCharacteristicsCode listID="CH">0</ProductTaxationCharacteristicsCode>
          </ProductTax>
        </Item>
        <Item actionCode="01">
          <BusinessTransactionDocumentItemTypeCode>002</BusinessTransactionDocumentItemTypeCode>
          <SHORT_Description>TravelPerDiem</SHORT_Description>
          <GrossAmount>375</GrossAmount>
          <AccountingCodingBlockDistribution>
            <GeneralLedgerAccountAliasCode>5050.20</GeneralLedgerAccountAliasCode>
            <TotalAmount>375</TotalAmount>
            <AccountingCodingBlockAssignment>
              <Amount>375</Amount>
              <GeneralLedgerAccountAliasCode>5050.20</GeneralLedgerAccountAliasCode>
              <ProjectTaskKey>
                <TaskID>0</TaskID>
              </ProjectTaskKey>
            </AccountingCodingBlockAssignment>
            <EmployeeID>114885</EmployeeID>
          </AccountingCodingBlockDistribution>
          <ProductTax>
            <ProductTaxationCharacteristicsCode listID="CH">0</ProductTaxationCharacteristicsCode>
          </ProductTax>
        </Item>
        <Item actionCode="01">
          <BusinessTransactionDocumentItemTypeCode>002</BusinessTransactionDocumentItemTypeCode>
          <SHORT_Description>Donation</SHORT_Description>
          <GrossAmount>225</GrossAmount>
          <AccountingCodingBlockDistribution>
            <GeneralLedgerAccountAliasCode>3020.26</GeneralLedgerAccountAliasCode>
            <TotalAmount>225</TotalAmount>
            <AccountingCodingBlockAssignment>
              <Amount></Amount>
              <GeneralLedgerAccountAliasCode>3020.26</GeneralLedgerAccountAliasCode>
            </AccountingCodingBlockAssignment>
          </AccountingCodingBlockDistribution>
          <ProductTax>
            <ProductTaxationCharacteristicsCode>976</ProductTaxationCharacteristicsCode>
          </ProductTax>
        </Item>
        <Item>
          <BusinessTransactionDocumentItemTypeCode>002</BusinessTransactionDocumentItemTypeCode>
          <SHORT_Description>Travel Expenses  Others</SHORT_Description>
          <GrossAmount>30</GrossAmount>
          <AccountingCodingBlockDistribution>
            <GeneralLedgerAccountAliasCode>5050.25</GeneralLedgerAccountAliasCode>
            <TotalAmount>30</TotalAmount>
            <AccountingCodingBlockAssignment>
              <Amount>30</Amount>
              <GeneralLedgerAccountAliasCode>5050.25</GeneralLedgerAccountAliasCode>
              <ProjectTaskKey>
                <TaskID>0</TaskID>
              </ProjectTaskKey>
            </AccountingCodingBlockAssignment>
            <EmployeeID>100207</EmployeeID>
          </AccountingCodingBlockDistribution>
          <ProductTax>
            <ProductTaxationCharacteristicsCode listID="CH">220</ProductTaxationCharacteristicsCode>
          </ProductTax>
        </Item>
        <Item>
          <BusinessTransactionDocumentItemTypeCode>002</BusinessTransactionDocumentItemTypeCode>
          <SHORT_Description>Mileage</SHORT_Description>
          <GrossAmount>70.12</GrossAmount>
          <AccountingCodingBlockDistribution>
            <GeneralLedgerAccountAliasCode>5050.25</GeneralLedgerAccountAliasCode>
            <TotalAmount>70.12</TotalAmount>
            <AccountingCodingBlockAssignment>
              <Amount>70.12</Amount>
              <GeneralLedgerAccountAliasCode>5050.25</GeneralLedgerAccountAliasCode>
              <ProjectTaskKey>
                <TaskID>AOEEP</TaskID>
              </ProjectTaskKey>
            </AccountingCodingBlockAssignment>
            <EmployeeID>100207</EmployeeID>
          </AccountingCodingBlockDistribution>
          <ProductTax>
            <ProductTaxationCharacteristicsCode listID="CH">220</ProductTaxationCharacteristicsCode>
          </ProductTax>
        </Item>
      </SupplierInvoice>
    </glob:SupplierInvoiceBundleMaintainRequest_sync>
  </soap:Body>
</soap:Envelope>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parseText(is.text)
    def supplierInvoice = root.Body.SupplierInvoiceBundleMaintainRequest_sync.SupplierInvoice

    supplierInvoice.Item.each() { 
        def amountNode = it.AccountingCodingBlockDistribution.AccountingCodingBlockAssignment.Amount
        if (!amountNode.text() || amountNode.text() == "") {
            it.replaceNode{}
        }
    }
    // .each() { it.replaceNode{} 

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));

    dataContext.storeStream(is, props);
}
