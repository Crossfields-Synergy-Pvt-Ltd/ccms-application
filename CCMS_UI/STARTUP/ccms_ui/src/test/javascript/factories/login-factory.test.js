describe('loginFactory', function() {
    var $httpBackend, loginFactory;

    beforeEach(module('loginFactory'));

    beforeEach(inject(function(_$httpBackend_, _loginFactory_) {
        $httpBackend = _$httpBackend_;
        loginFactory = _loginFactory_;
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('login_user', function() {
        it('should call the correct URL with query params', function() {
            $httpBackend.expectGET('/CCMS/superadmin/user/login?name=test@test.com&password=pass123')
                .respond({ status: '100' });

            loginFactory.login_user('?name=test@test.com&password=pass123');
        });

        it('should return user data on success', function() {
            var responseData = { status: '100', email: 'test@test.com', role: 'SUPER ADMIN' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=test@test.com&password=pass123')
                .respond(responseData);

            var result;
            loginFactory.login_user('?name=test@test.com&password=pass123')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(responseData);
        });

        it('should handle empty query string', function() {
            $httpBackend.expectGET('/CCMS/superadmin/user/login')
                .respond({ status: '00' });

            loginFactory.login_user('');
        });

        it('should handle server error gracefully', function() {
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=error')
                .respond(500, 'Server Error');

            loginFactory.login_user('?name=error');
        });
    });
});
