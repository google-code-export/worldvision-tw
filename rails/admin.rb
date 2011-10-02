require 'sinatra'
require 'dm-core'
require 'rails/model/account'

# Configure DataMapper to use the App Engine datastore 
DataMapper.setup(:default, "appengine://auto")

# Make sure our template can use <%=h
helpers do
  include Rack::Utils
  alias_method :h, :escape_html
end

get '/w_admin' do
  # Just list all the shouts
  @accounts = Account.all
  erb :admin_index
end

post '/create_account' do
  account = Account.create(:account=>params[:account], :password=>params[:password], :role=>params[:role],
    :name=>params[:name],:voulenteer_id=>params[:volenteer_id], :voulenteer_type=>params[:volenteer_type])
  redirect '/w_admin'
end

__END__

@@ admin_index
