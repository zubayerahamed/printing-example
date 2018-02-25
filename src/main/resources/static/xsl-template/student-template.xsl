<?xml version="1.0" encoding="UTF-8"?>
<!--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!-->
<!--!                                                              !-->
<!--!    File Name    : student-template.xsl                       !-->
<!--!    Description  : Student template (customized)              !-->
<!--!    Author       : Zubayer Ahamed                             !-->
<!--!    Date         : 25-Feb-2018                                !-->
<!--!    Copyright    : Copyright (c) coderslab-bd.com             !-->
<!--!                                                              !-->
<!--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!-->
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo">
	<xsl:template match="students">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block width="100%" font-size="16pt" font-weight="bold"
						xsl:use-attribute-sets="text.box">
						Coders Lab Bangladesh
					</fo:block>
					<fo:block font-size="10pt">
						<fo:table table-layout="fixed" width="100%"
							border-collapse="separate" xsl:use-attribute-sets="text.box">
							<fo:table-column column-width="30%" />
							<fo:table-column column-width="30%" />
							<fo:table-column column-width="40%" />
							<fo:table-header>
								<fo:table-row>
									<fo:table-cell xsl:use-attribute-sets="table.th">
										<fo:block font-weight="bold">ID</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="table.th">
										<fo:block font-weight="bold">NAME</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="table.th">
										<fo:block font-weight="bold">Email</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>
								<xsl:apply-templates select="student" />
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="student">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="table.td">
				<fo:block>
					<xsl:value-of select="id" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="table.td">
				<fo:block>
					<xsl:value-of select="name" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="table.td">
				<fo:block>
					<xsl:value-of select="email" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>



	<!--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	<!--! Attribute set / CSS ! -->
	<!--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->

	<xsl:attribute-set name="text.box">
		<xsl:attribute name="font-size">7pt</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="display-align">center</xsl:attribute>
		<xsl:attribute name="padding">2px</xsl:attribute>
		<xsl:attribute name="border">1px solid #000000</xsl:attribute>
		<xsl:attribute name="margin-bottom">20px</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table.th">
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="display-align">center</xsl:attribute>
		<xsl:attribute name="padding">2px</xsl:attribute>
		<xsl:attribute name="border">1px solid #000000</xsl:attribute>
		<xsl:attribute name="background-color">#DDD</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table.td">
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="display-align">center</xsl:attribute>
		<xsl:attribute name="padding">2px</xsl:attribute>
		<xsl:attribute name="border">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="text.header">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="background-color">#E6489C</xsl:attribute>
		<xsl:attribute name="color">#000000</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.rightWhite">
		<xsl:attribute name="border-right">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.right">
		<xsl:attribute name="border-right">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.top">
		<xsl:attribute name="border-top">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.left">
		<xsl:attribute name="border-left">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.bottom">
		<xsl:attribute name="border-bottom">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.all">
		<xsl:attribute name="border">1px solid #000000</xsl:attribute>
	</xsl:attribute-set>
</xsl:stylesheet>