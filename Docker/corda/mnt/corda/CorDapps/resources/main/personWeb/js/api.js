
function createPerson(){

    var endpoint = '/api/person/create';

    var name = $('#create-name').val();
    var document = $('#create-document').val();

    var payload = {
            name : name,
            document : document
        }

    $.ajax({
       url: endpoint,
       type: 'PUT',
       headers: {
           "content-type": 'application/json',
       },
       data: JSON.stringify( payload ),
       success: function(response) {
         loadPeople();
       }
    }).fail(function (jqXHR, textStatus, error) {
        console.log('Status: ', textStatus);
        console.log('Error: ', error);
        console.log('Message: ', jqXHR.responseText);
     });

}

function loadPeople(){

    $('#personTable').find('tr').remove()
    $('#personTable').append('<tr><td>ID</td><td>Nome</td><td>Documento</td></tr>');

    var endpoint = '/api/person/all';

    $.get(endpoint, function(data, status){

        data.forEach(function(item, index){

            $('#personTable').append('<tr>' +
            '<td>'+ item.state.data.linearId.id +'</td>' +
            '<td>'+ item.state.data.person.name +'</td>' +
            '<td>'+ item.state.data.person.document +'</td>' +
            '</tr>');


        });

    });

}