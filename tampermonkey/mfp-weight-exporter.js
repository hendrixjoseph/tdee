// ==UserScript==
// @name         MyFitnessPal Weight Exporter
// @namespace    https://www.joehxblog.com/
// @version      2024-01-02
// @description  try to take over the world!
// @author       JoeHx
// @match        https://www.myfitnesspal.com/measurements/edit*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=myfitnesspal.com
// @grant        none
// ==/UserScript==

(function() {
    'use strict';

    const data = [];

    const session = '~';
    const mainUrl = `https://www.myfitnesspal.com/_next/data/${session}/en/measurements/edit.json`;

    const getData = url => fetch(url)
        .then(r => r.json())
        .then(r => r.pageProps.dehydratedState.queries[6].state.data)
        .then(r => {
            data.push(r.items.map(item => [item.date, item.value]));
            return r.has_more;
        });;

    const getDataForPage = page => getData(`${mainUrl}?type=Weight&page=${page}`);

    const getAllData = currentPage => getDataForPage(currentPage)
       .then(has_more => {
           if(has_more) {
               return getAllData(currentPage + 1);
           } else {
               return false
           }
       });

    const downloadCsv = body => {
      const filename = 'weight-data-' + Date.now() + '.csv';

      const header = 'date,weight\n';

      let downloadLink = document.createElement('a');
      downloadLink.style = 'color: white; margin: 0 1rem;'
      downloadLink.href = 'data:text/csv;charset=utf-8,' + encodeURI(header + body);
      downloadLink.target = '_blank';
      downloadLink.download = filename;
      downloadLink.textContent = filename;

      downloadLink.click();
    }

    getAllData(1)
        .then(() => data.flat())
        .then(rows => rows.map(row => row.join(',')).join('\n'))
        .then(csv => downloadCsv(csv));
})();