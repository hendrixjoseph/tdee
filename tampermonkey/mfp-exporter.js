// ==UserScript==
// @name         MyFitnessPal Exporter
// @namespace    https://www.joehxblog.com/
// @version      2024-01-24
// @description  try to take over the world!
// @author       JoeHx
// @match        https://www.myfitnesspal.com/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=myfitnesspal.com
// @grant        none
// ==/UserScript==

(function() {
    'use strict';

    const div = document.createElement('div');
    div.style = 'position: fixed; top: 60px; left: 200px; z-index: 1000; background: white; border: 1px solid black; width: 200px; padding: 1rem; text-align: start;';

    const button = document.createElement('button');
    button.textContent = 'Export';
    button.style = 'display: block; background: rgb(240, 240, 240); border: 1px solid black; padding: 1px 6px;';
    button.disabled = true;

    const dater = text => {
        const label = document.createElement('label');
        label.innerText = text;
        label.style = 'margin-right: 0.5rem;'

        const date = document.createElement('input');
        date.type = 'date';
        date.style = 'padding-start: 1px;';

        const span = document.createElement('span');
        span.style = 'display: block; margin-bottom: 1rem;';
        span.append(label);
        span.append(date);

        return span;
    }

    const start = dater('start');
    const end = dater('end');

    button.onclick = () => {
        const startDate = new Date(start.querySelector('input').value);
        const endDate = new Date(end.querySelector('input').value);

        const map = new Map();
        const promises = [];

        for (let day = new Date(startDate); day <= endDate; day.setDate(day.getDate() + 1)) {
            const dateArray = day.toLocaleDateString().split('/');
            const dateString = [dateArray[2], dateArray[0], dateArray[1]].map(s => s.length === 1 ? `0${s}` : s).join('-');

            promises.push(
                fetch(`https://www.myfitnesspal.com/food/diary?date=${dateString}`)
                    .then(r => r.text())
                    .then(html => new DOMParser().parseFromString(html, "text/html"))
                    .then(doc => [doc.querySelector('.total td:nth-child(2)'), doc.querySelector('.extra')])
                    .then(tds => tds.map(td => td?.innerText.replace(/\D/g,'') ?? ''))
                    .then(remaining => map.set(dateString, remaining))
            );
        }

        Promise.all(promises).then(() => {
            console.log('done!');
            console.log(map);

            const filename = 'net-calorie-data-' + Date.now() + '.csv';

            const header = 'date,food,exercise\n';
            const body = Array.from(map).sort().reverse().map(entry => entry.flat()).map(entry => entry.map(e => `"${e}"`).join(',')).join('\n');

            let downloadLink = document.createElement('a');
            downloadLink.innerText = `Download ${map.size} results!`;
            downloadLink.style = 'display: block;'
            downloadLink.href = 'data:text/csv;charset=utf-8,' + encodeURI(header + body);
            downloadLink.target = '_blank';
            downloadLink.download = filename;
            downloadLink.textContent = filename;

            div.append(downloadLink);
        });
    }

    const inputs = [start, end].map(d => d.querySelector('input'));

    inputs.forEach(i => {
       i.oninput = () => {
           button.disabled = !(inputs.map(i => i.value).every(v => v) && new Date(inputs[0].value) < new Date(inputs[1].value));
       };
    });

    div.append(start);
    div.append(end);
    div.append(button);

    document.body.append(div);
})();