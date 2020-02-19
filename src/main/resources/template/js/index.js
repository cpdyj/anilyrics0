'use strict';
const drawer = new mdui.Drawer('#left-drawer', { 'swipe': true });
getDataWithOffset(0, 20)
function addTableListener() {
    document.querySelectorAll('td').forEach(it => it.addEventListener('click', it => {
        let type = it.target.dataset.type
        // if (type === undefined) type = it.target.parentElement.dataset.type
        let id = it.target.parentElement.dataset.id
        // if (id === undefined) id = it.target.parentElement.parentElement.dataset.id
        console.log(type + ' ' + id)
    }))
};

function buildTable(data) {
    return `
        <tr data-id="${data.id}">
            <td class="mdui-ripple" data-type="title">${data.title}</td>
            <td class="mdui-ripple" data-type="artist">${data.artist}</td>
            <td class="mdui-ripple" data-type="lyricist">${data.lyricist}</td>
            <td class="mdui-ripple" data-type="composer">${data.composer}</td>
            <td class="mdui-ripple" data-type="category">${data.category}</td>
            // <td class="mdui-ripple"data-type="favourite"><i class="mdui-icon material-icons">favorite</i></td>
        </tr>
        `
}

function getDataWithOffset(offset, maxSize) {
    let request = new XMLHttpRequest()
    request.open('POST', './list')
    request.withCredentials = true
    request.send(JSON.stringify({
        offset: parseInt(offset),
        maxSize: parseInt(maxSize)
    }))
    request.onreadystatechange = function () {
        if (request.readyState == XMLHttpRequest.DONE) {
            let t = ''
            if (request.status != 200) {
                mdui.snackbar({ message: `Error[${request.status}]: ${request.statusText}` })
                return
            }
            JSON.parse(request.responseText).data.forEach(e => t += buildTable(e))
            document.querySelector('#list tbody').innerHTML = t
            addTableListener()
        }
    }
}
addTableListener()
