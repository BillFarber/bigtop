xquery version "1.0-ml";

module namespace cac = "http://com.marklogic.bigtop.cac";

declare namespace search = "http://marklogic.com/appservices/search";
declare namespace cts    = "http://marklogic.com/cts";

declare function cac:snippet(
   $result as node(),
   $ctsquery as schema-element(cts:query),
   $options as element(search:transform-results)?
) as element(search:snippet) {
	let $modulation := $ctsquery/cts:json-property-value-query/cts:value/text()
	let $minimumFrequency := $ctsquery/cts:json-property-range-query[@operator="&gt;="]/cts:value/text()
	let $maximumFrequency := $ctsquery/cts:json-property-range-query[@operator="&lt;="]/cts:value/text()
	let $spectrumNode := $result/store/uiSpectrums/*[signals/modulation=$modulation and signals/freq/data() >= $minimumFrequency and signals/freq/data() <= $maximumFrequency]
	return
    element search:snippet {
        attribute format { "json" },
        text {fn:concat('{"spectrum-node": ', xdmp:to-json-string($spectrumNode), '}')}
    }
};