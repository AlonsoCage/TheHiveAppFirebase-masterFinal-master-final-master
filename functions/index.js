var functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const actionTypeNewLike = "new_like"
const actionTypeNewComment = "new_comment"
const actionTypeNewPost = "new_post"
const notificationTitle = "Social App"

const postsTopic = "postsTopic"

exports.pushNotificationLikes = functions.database.ref('/post-likes/{postId}/{authorId}/{likeId}').onWrite(event => {

    console.log('New like was added');

    const likeAuthorId = event.params.authorId;
    const postId = event.params.postId;

   
    const getPostTask = admin.database().ref(`/posts/${postId}`).once('value');

    return getPostTask.then(post => {

        if (likeAuthorId == post.val().authorId) {
            return console.log('User liked own post');
        }

        
        const getDeviceTokensTask = admin.database().ref(`/profiles/${post.val().authorId}/notificationTokens`).once('value');
        console.log('getDeviceTokensTask path: ', `/profiles/${post.val().authorId}/notificationTokens`)

        
        const getLikeAuthorProfileTask = admin.database().ref(`/profiles/${likeAuthorId}`).once('value');

        Promise.all([getDeviceTokensTask, getLikeAuthorProfileTask]).then(results => {
            const tokensSnapshot = results[0];
            const likeAuthorProfile = results[1].val();

           
            if (!tokensSnapshot.hasChildren()) {
                return console.log('There are no notification tokens to send to.');
            }

            console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
            console.log('Fetched like Author profile', likeAuthorProfile);

            
            const payload = {
                data : {
                    actionType: actionTypeNewLike,
                    title: notificationTitle,
                    body: `${likeAuthorProfile.username} liked your post`,
                    icon: post.val().imagePath,
                    postId: postId,

                },
            };

           
            const tokens = Object.keys(tokensSnapshot.val());
            console.log('tokens:', tokens[0]);

           
            return admin.messaging().sendToDevice(tokens, payload).then(response => {
                        
                        const tokensToRemove = [];
                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Failure sending notification to', tokens[index], error);
                       
                        if (error.code === 'messaging/invalid-registration-token' ||
                            error.code === 'messaging/registration-token-not-registered') {
                            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                        }
                    }
                });
                return Promise.all(tokensToRemove);
            });
        });
    })
});

exports.pushNotificationComments = functions.database.ref('/post-comments/{postId}/{commentId}').onWrite(event => {

    const commentId = event.params.commentId;
    const postId = event.params.postId;
    const comment = event.data.val();

    console.log('New comment was added, id: ', postId);

    
    const getPostTask = admin.database().ref(`/posts/${postId}`).once('value');

    return getPostTask.then(post => {



       
        const getDeviceTokensTask = admin.database().ref(`/profiles/${post.val().authorId}/notificationTokens`).once('value');
        console.log('getDeviceTokensTask path: ', `/profiles/${post.val().authorId}/notificationTokens`)

        
        const getCommentAuthorProfileTask = admin.database().ref(`/profiles/${comment.authorId}`).once('value');
        console.log('getCommentAuthorProfileTask path: ', `/profiles/${comment.authorId}`)

        Promise.all([getDeviceTokensTask, getCommentAuthorProfileTask]).then(results => {
            const tokensSnapshot = results[0];
            const commentAuthorProfile = results[1].val();

            if (commentAuthorProfile.id == post.val().authorId) {
                return console.log('User commented own post');
            }

           
            if (!tokensSnapshot.hasChildren()) {
                return console.log('There are no notification tokens to send to.');
            }

            console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');

           
            const payload = {
                data : {
                    actionType: actionTypeNewComment,
                    title: notificationTitle,
                    body: `${commentAuthorProfile.username} commented your post`,
                    icon: post.val().imagePath,
                    postId: postId,
                },
            };

           
            const tokens = Object.keys(tokensSnapshot.val());
            console.log('tokens:', tokens[0]);

        
            return admin.messaging().sendToDevice(tokens, payload).then(response => {
                        
                        const tokensToRemove = [];
                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Failure sending notification to', tokens[index], error);
                        /
                        if (error.code === 'messaging/invalid-registration-token' ||
                            error.code === 'messaging/registration-token-not-registered') {
                            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                        }
                    }
                });
                return Promise.all(tokensToRemove);
            });
        });
    })
});

exports.pushNotificationNewPost = functions.database.ref('/posts/{postId}').onWrite(event => {
    const postId = event.params.postId;

   
    if (event.data.previous.exists()) {
        console.log('Post was changed');
        return;
    }
  
    if (!event.data.exists()) {
        console.log('Post was removed');
        return;
    }

    console.log('New post was created');

    
    const getAuthorIdTask = admin.database().ref(`/posts/${postId}/authorId`).once('value');

     return getAuthorIdTask.then(authorId => {

        console.log('post author id', authorId.val());

        
        const payload = {
            data : {
                actionType: actionTypeNewPost,
                postId: postId,
                authorId: authorId.val(),
            },
        };

       
        return admin.messaging().sendToTopic(postsTopic, payload)
                 .then(function(response) {
                  
                   console.log("Successfully sent info about new post :", response);
                 })
                 .catch(function(error) {
                   console.log("Error sending info about new post:", error);
                 });
         });

});


